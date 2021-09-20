package com.clane.app.wallet.transaction;

import com.clane.app.core.data.Active;
import com.clane.app.core.data.Data;
import com.clane.app.wallet.transaction.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Table(name = "clane_txn")
@Entity
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Active
public class Transaction extends Data {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String txnRef;
    @Temporal(TemporalType.DATE)
    private Date txnDate;
    @Column(nullable = false)
    private String senderNo;
    @Column(nullable = false)
    private String sender;
    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private String receiverNo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnType txnType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnActivity activity;
    @Column(precision = 31, scale = 2)
    private BigDecimal amount;
    @Column(precision = 31, scale = 2)
    private BigDecimal availableBalance;
    @Column(precision = 31, scale = 2)
    private BigDecimal totalBalance;

    public Transaction mapDto(TransactionDto dto) {
        return Transaction.builder()
                .totalBalance(dto.getTotalBalance())
                .availableBalance(dto.getAvailableBalance())
                .amount(dto.getAmount())
                .txnType(dto.getTxnType())
                .activity(dto.getActivity())
                .sender(dto.getSender())
                .senderNo(dto.getSenderNo())
                .receiver(dto.getReceiver())
                .receiverNo(dto.getReceiverNo())
                .build();
    }
}
