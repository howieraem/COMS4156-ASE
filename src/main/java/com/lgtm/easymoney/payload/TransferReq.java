package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.Category;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferReq {
    @NotNull
    private Long fromUid;

    @NotNull
    private Long toUid;

    @NotNull
    @Digits(integer = 100, fraction = 2)
    @DecimalMin(value = "0.0",inclusive = false)
    private BigDecimal amount;

    private String description;

    private Category category;
}