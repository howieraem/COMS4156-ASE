package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.Category;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RequestAcceptDeclineReq {
    @NotNull
    private Long fromUid;
    @NotNull
    private Long toUid;
    @NotNull
    private Long requestID;
}
