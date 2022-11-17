package com.lgtm.easymoney.payload.rsp;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * balance response payload.
 */
@AllArgsConstructor
@Getter
public class BalanceRsp {
  private BigDecimal currBalance;
}
