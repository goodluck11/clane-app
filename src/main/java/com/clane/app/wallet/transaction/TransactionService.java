package com.clane.app.wallet.transaction;

import com.clane.app.core.utils.Utils;
import com.clane.app.wallet.transaction.dto.PostingResult;
import com.clane.app.wallet.transaction.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public PostingResult postTransactions(TransactionDto debit, TransactionDto credit) { // posting debit and credit
        String txnRef = Utils.generateRandomString();
        Date txnDate = new Date();

        Transaction debitTxn = new Transaction().mapDto(debit);
        debitTxn.setTxnDate(txnDate);
        debitTxn.setTxnRef(txnRef);

        Transaction creditTxn = new Transaction().mapDto(credit);
        creditTxn.setTxnDate(txnDate);
        creditTxn.setTxnRef(txnRef);

        List<Transaction> transactions = Arrays.asList(debitTxn, creditTxn);
        List<Transaction> savedTxnList = this.transactionRepository.saveAll(transactions);
        return PostingResult.builder()
                .debit(savedTxnList.get(0))
                .credit(savedTxnList.get(1))
                .build();
    }

    public BigDecimal findDailyTransaction(String phoneNo, String email, TxnActivity activity) {
        List<Transaction> transactions = this.transactionRepository.findDailyTransaction(activity.getName(), phoneNo, email, TxnType.DEBIT.name());
        System.out.println(transactions.size());
        if (ObjectUtils.isEmpty(transactions)) {
            return BigDecimal.ZERO;
        }
        return transactions.stream()
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
