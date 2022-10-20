package com.lgtm.easymoney.payload;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * balance response payload.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BalanceRsp {
  private BigDecimal currBalance;
}
