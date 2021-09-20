package com.clane.app.wallet;

import com.clane.app.core.data.ClaneResponse;
import com.clane.app.core.data.ResponseMessages;
import com.clane.app.wallet.dto.TopUp;
import com.clane.app.wallet.dto.Transfer;
import com.clane.app.wallet.transaction.Transaction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value="Api for carrying out wallet related activities")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/topUp")
    @ApiOperation(value = "For manual top up of a clane user wallet", response = ClaneResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = ResponseMessages.TOP_UP_SUCCESS),
    })
    public ResponseEntity<ClaneResponse> walletTopUp(@Valid @RequestBody TopUp topUp) {
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(ResponseMessages.TOP_UP_SUCCESS)
                .status(HttpStatus.CREATED.value())
                .payload(walletService.topUp(topUp))
                .build());
    }

    @PostMapping("/transfer")
    @ApiOperation(value = "For transfer between two registered clane user wallets", response = ClaneResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = ResponseMessages.TRANSFER_SUCCESS),
    })
    public ResponseEntity<ClaneResponse> transfer(@Valid @RequestBody Transfer transfer) {
        Transaction transaction = walletService.transfer(transfer);
        return ResponseEntity.ok(ClaneResponse.builder()
                .message(MessageFormat.format(ResponseMessages.TRANSFER_SUCCESS, transaction.getReceiver()))
                .status(HttpStatus.CREATED.value())
                .payload(transaction)
                .build());
    }
}
