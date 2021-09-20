package com.clane.app.wallet.transaction.dto;

import com.clane.app.core.data.Data;
import com.clane.app.wallet.transaction.TxnActivity;
import com.clane.app.wallet.transaction.TxnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto extends Data {
    private String senderNo;
    private String sender;
    private String receiver;
    private String receiverNo;
    private TxnType txnType;
    private TxnActivity activity;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private BigDecimal totalBalance;
}
