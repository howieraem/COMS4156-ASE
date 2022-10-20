package com.lgtm.easymoney.payload;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * transfer response.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransferRsp {
  private Boolean success;
  private String message;
  private List<TransactionRsp> transfers;
  private BigDecimal currBalance;
}
