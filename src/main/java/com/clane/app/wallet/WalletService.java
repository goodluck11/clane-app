package com.clane.app.wallet;

import com.clane.app.core.data.Data;
import com.clane.app.core.exception.DataNotFoundException;
import com.clane.app.core.exception.InsufficientFundException;
import com.clane.app.core.exception.LimitExceededException;
import com.clane.app.security.user.User;
import com.clane.app.security.user.UserService;
import com.clane.app.settings.kyc.KycLevel;
import com.clane.app.settings.kyc.KycLevelService;
import com.clane.app.wallet.dto.TopUp;
import com.clane.app.wallet.dto.Transfer;
import com.clane.app.wallet.transaction.Transaction;
import com.clane.app.wallet.transaction.TransactionService;
import com.clane.app.wallet.transaction.TxnActivity;
import com.clane.app.wallet.transaction.TxnType;
import com.clane.app.wallet.transaction.dto.PostingResult;
import com.clane.app.wallet.transaction.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

@Service
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final KycLevelService kycLevelService;
    private final TransactionService transactionService;

    public WalletService(WalletRepository walletRepository, UserService userService,
                         TransactionService transactionService, KycLevelService kycLevelService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.kycLevelService = kycLevelService;
        this.transactionService = transactionService;
    }


    public Wallet createWallet(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .availableBalance(BigDecimal.ZERO)
                .totalBalance(BigDecimal.ZERO)
                .build();
        log.info("Creating user wallet {}", wallet.toString());
        Wallet saved = walletRepository.save(wallet);
        log.info("Wallet successfully created");
        return saved;
    }

    private Wallet findUserWallet(User user) {
        log.info("Fetching user wallet...");
        Optional<Wallet> wallet = this.walletRepository.findByUser_EmailAndUser_PhoneNumber(user.getEmail(), user.getPhoneNumber());
        return wallet.orElseGet(() -> createWallet(user));
    }

    public User validateUserWalletReference(String walletNo) {
        Optional<User> user = this.userService.findByEmailOrPhoneNumber(walletNo);
        return user.orElseThrow(() -> new DataNotFoundException("User with wallet ref " + walletNo + " not found"));
    }

    @Transactional
    public Transaction topUp(TopUp topUp) {
        User user = validateUserWalletReference(topUp.getWalletNo()); // validate user wallet ref
        Wallet wallet = findUserWallet(user); // find user wallet
        wallet = creditWallet(wallet, topUp.getAmount()); // credit user wallet

        TransactionDto credit = TransactionDto.builder()
                .activity(TxnActivity.TOP_UP).txnType(TxnType.CREDIT).amount(topUp.getAmount()).availableBalance(wallet.getAvailableBalance())
                .totalBalance(wallet.getTotalBalance()).receiver(user.fullName()).receiverNo(topUp.getWalletNo()).sender(Data.APP_NAME).senderNo(Data.DEFAULT_WALLET).build();

        TransactionDto debit = TransactionDto.builder()
                .activity(TxnActivity.TOP_UP).txnType(TxnType.DEBIT).amount(topUp.getAmount()).availableBalance(wallet.getAvailableBalance())
                .totalBalance(wallet.getTotalBalance()).receiver(user.fullName()).receiverNo(topUp.getWalletNo()).sender(Data.APP_NAME).senderNo(Data.DEFAULT_WALLET).build();

        PostingResult result = this.transactionService.postTransactions(debit, credit); // post debit and credit transaction

        return result.getCredit();
    }

    @Transactional
    public Transaction transfer(Transfer transfer) {
        // sender number could have been gotten from current logged in user using security context if security was included
        // validate txn pin if provided
        User sender = validateUserWalletReference(transfer.getSenderNo()); // validate sender wallet ref exists
        Wallet senderWallet = findUserWallet(sender);

        validateSufficientFunds(senderWallet, transfer.getAmount()); // validate sender wallet for sufficient funds

        User recipient = validateUserWalletReference(transfer.getRecipientNo()); // validate recipient wallet ref exists
        Wallet recipientWallet = findUserWallet(recipient);

        validateTransferLimit(transfer, sender); // validate sender transfer limit

        senderWallet = debitWallet(senderWallet, transfer.getAmount()); // debit sender wallet
        TransactionDto debit = TransactionDto.builder()
                .activity(TxnActivity.WALLET_TRANSFER).txnType(TxnType.DEBIT).amount(transfer.getAmount()).availableBalance(senderWallet.getAvailableBalance())
                .totalBalance(senderWallet.getTotalBalance()).receiver(recipient.fullName()).receiverNo(transfer.getRecipientNo())
                .sender(sender.fullName()).senderNo(transfer.getSenderNo()).build();

        recipientWallet = creditWallet(recipientWallet, transfer.getAmount()); // credit recipient wallet
        TransactionDto credit = TransactionDto.builder()
                .activity(TxnActivity.WALLET_TRANSFER).txnType(TxnType.CREDIT).amount(transfer.getAmount()).availableBalance(recipientWallet.getAvailableBalance())
                .totalBalance(recipientWallet.getTotalBalance()).receiver(recipient.fullName()).receiverNo(transfer.getRecipientNo())
                .sender(sender.fullName()).senderNo(transfer.getSenderNo()).build();

        PostingResult result = this.transactionService.postTransactions(debit, credit); // post debit and credit transaction

        return result.getDebit();
    }

    private void validateTransferLimit(Transfer transfer, User sender) {
        KycLevel kycLevel = this.kycLevelService.findByCode(sender.getLevelCode());
        BigDecimal limit = kycLevel.getWalletTrfLimit();
        BigDecimal dailyTotal = this.transactionService.findDailyTransaction(sender.getPhoneNumber(), sender.getEmail(), TxnActivity.WALLET_TRANSFER);
        System.out.println("Daily: " + dailyTotal);
        BigDecimal totalAmount = dailyTotal.add(transfer.getAmount());
        if ((limit.compareTo(new BigDecimal(-1)) != 0) && (totalAmount.compareTo(limit) > 0)) {
            BigDecimal excess = totalAmount.subtract(limit);
            String msg = MessageFormat.format("Your transfer limit of {0} has been exceeded by {1}", limit, excess);
            throw new LimitExceededException(msg);
        }
    }

    private void validateSufficientFunds(Wallet senderWallet, BigDecimal amount) {
        boolean sufficient = senderWallet.getAvailableBalance().compareTo(amount) > 0;
        if (!sufficient) {
            throw new InsufficientFundException("Your wallet balance is insufficient");
        }
    }

    private Wallet creditWallet(Wallet wallet, BigDecimal amount) {
        KycLevel kycLevel = this.kycLevelService.findByCode(wallet.getUser().getLevelCode());
        WalletService.calculateMaxBalance(wallet, amount, kycLevel.getMaxBalance());
        wallet = this.walletRepository.save(wallet);
        return wallet;
    }

    private Wallet debitWallet(Wallet wallet, BigDecimal amount) {
        wallet.setTotalBalance(wallet.getTotalBalance().add(amount));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet = this.walletRepository.save(wallet);
        return wallet;
    }

    private static void calculateMaxBalance(Wallet wallet, BigDecimal amount, BigDecimal maxBalance) {
        BigDecimal total = wallet.getAvailableBalance().add(amount); // add the new amount to the available balance

        // check max balance for user kyc level. if -1, then it's unlimited but if it's not -1 & the total is > max balance,
        // hold the money in his current balance and make the max balance available for spending until the user upgrades level
        if ((maxBalance.compareTo(new BigDecimal(-1)) != 0) && total.compareTo(maxBalance) > 0) {
            wallet.setTotalBalance(wallet.getTotalBalance().add(amount));
            wallet.setAvailableBalance(maxBalance);
        } else {
            wallet.setTotalBalance(wallet.getTotalBalance().add(amount));
            wallet.setAvailableBalance(total);
        }
    }

}
