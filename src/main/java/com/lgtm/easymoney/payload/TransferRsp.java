package com.lgtm.easymoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransferRsp {
    private Boolean success;
    private BigDecimal currBalance;
}