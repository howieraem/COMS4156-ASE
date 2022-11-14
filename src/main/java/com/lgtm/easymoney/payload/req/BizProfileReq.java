package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

/** Payload containing new business profile info. */
@Getter
public class BizProfileReq {
  @NotBlank
  String promotionText;
}
