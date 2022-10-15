package com.lgtm.easymoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestRsp {
    // maybe we don't need this, simply reuse balance rsp
    private Boolean success;
    private BigDecimal currBalance;
}
