package com.lgtm.easymoney.payload.req;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.validator.ValueOfEnum;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * request for making a request.
 */
@Data
public class RequestReq {
  @NotNull
  private Long toUid;
  @NotNull
  @Digits(integer = 100, fraction = 2)
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal amount;

  private String description;
  @ValueOfEnum(enumClass = Category.class)
  private String category;
}
