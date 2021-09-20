package com.clane.app.wallet.dto;

import com.clane.app.core.data.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
public class TopUp extends Data {
    @NotBlank(message = "Wallet number is required")
    private String walletNo;
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount cannot be less than 1")
    private BigDecimal amount;
}
