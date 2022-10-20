package com.lgtm.easymoney.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * register request payload.
 */
@Data
public class RegisterReq {
  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 1)
  private String password;

  @NotBlank
  private String userType;

  private String address;

  @Pattern(regexp = "[\\d]{10}")
  private String phone;

  @NotBlank
  private String accountName;

  /* Regarding account number lengths: https://qr.ae/pvJXyd */
  @NotBlank
  @Pattern(regexp = "[\\d]{1,17}")
  private String accountNumber;
  @NotBlank
  @Pattern(regexp = "[\\d]{9}")
  private String routingNumber;
}
