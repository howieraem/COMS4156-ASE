package com.lgtm.easymoney.payload;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BalanceReq {
    @NotNull
    private Long uid;

    @NotNull
    private Boolean isDeposit;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
}
