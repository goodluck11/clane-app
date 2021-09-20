package com.clane.app.wallet;

import com.clane.app.core.data.ClaneResponse;
import com.clane.app.core.data.ResponseMessages;
import com.clane.app.wallet.dto.TopUp;
import com.clane.app.wallet.dto.Transfer;
import com.clane.app.wallet.transaction.Transaction;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.MessageFormat;

@RestController
@RequestMapping("wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topUp")
    @ApiOperation("For manual top-up of a user wallet")
    public ResponseEntity<ClaneResponse> walletTopUp(@Valid @RequestBody TopUp topUp) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(ResponseMessages.TOP_UP_SUCCESS)
                .status(HttpStatus.CREATED.value())
                .payload(walletService.topUp(topUp)).build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<ClaneResponse> transfer(@Valid @RequestBody Transfer transfer) {
        Transaction transaction = walletService.transfer(transfer);
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(MessageFormat.format(ResponseMessages.TRANSFER_SUCCESS, transaction.getReceiver()))
                .status(HttpStatus.CREATED.value())
                .payload(transaction).build());
    }
}
