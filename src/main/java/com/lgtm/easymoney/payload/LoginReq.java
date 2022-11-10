package com.lgtm.easymoney.payload;

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
