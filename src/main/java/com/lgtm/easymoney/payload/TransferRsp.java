package com.lgtm.easymoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransferRsp {
    private Boolean success;
    private String message;
    private List<TransactionRsp> transfers;
    private BigDecimal currBalance;
}