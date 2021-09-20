package com.clane.app.wallet.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class Transfer extends com.clane.app.core.data.Data {
    @NotBlank(message = "Sender number is required")
    private String senderNo;
    @NotBlank(message = "Recipient number is required")
    private String recipientNo;
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount cannot be less than 1")
    private BigDecimal amount;
}
