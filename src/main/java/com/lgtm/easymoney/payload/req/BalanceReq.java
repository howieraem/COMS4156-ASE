package com.lgtm.easymoney.payload.req;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * balance request payload.
 */
@Data
public class BalanceReq {
  @NotNull
  @Digits(integer = 100, fraction = 2)
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal amount;
}
