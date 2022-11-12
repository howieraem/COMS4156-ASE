package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/** Login request. */
@Data
public class LoginReq {
  @NotBlank
  String email;

  @NotBlank
  String password;
}
