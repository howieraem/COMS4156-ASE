package com.lgtm.easymoney.payload.rsp;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * transfer response.
 */
@Data
@NoArgsConstructor
public class TransferRsp {
  private Boolean success;
  private String message;
  private List<TransactionRsp> transfers;
  private BigDecimal currBalance;
}
