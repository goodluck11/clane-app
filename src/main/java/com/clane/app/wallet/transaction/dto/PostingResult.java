package com.clane.app.wallet.transaction.dto;

import com.clane.app.core.data.Data;
import com.clane.app.wallet.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostingResult extends Data {
    private Transaction debit;
    private Transaction credit;
}
