package com.lgtm.easymoney.payload.req;

import com.lgtm.easymoney.configs.ValidationConsts;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.enums.validator.ValueOfEnum;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
  private String password;

  @ValueOfEnum(enumClass = UserType.class)
  private String userType;

  private String address;

  @Pattern(regexp = ValidationConsts.PHONE_NUMBER_REGEX)
  private String phone;

  @NotNull
  @Pattern(regexp = ValidationConsts.ACCOUNT_NAME_REGEX)
  private String accountName;

  @NotNull
  @Pattern(regexp = ValidationConsts.ACCOUNT_NUMBER_REGEX)
  private String accountNumber;

  @NotNull
  @Pattern(regexp = ValidationConsts.ROUTING_NUMBER_REGEX)
  private String routingNumber;

  private String bizPromotionText;
}
