package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/** Payload containing new business profile info. */
@Data
public class BizProfileReq {
  @NotBlank
  String promotionText;
}
