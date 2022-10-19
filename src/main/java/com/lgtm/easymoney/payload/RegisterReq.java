package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.configs.ValidationConsts;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.enums.validator.ValueOfEnum;
import lombok.Data;

import javax.validation.constraints.*;

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

    @NotBlank
    private String accountName;

    @NotNull
    @Pattern(regexp = ValidationConsts.ACCOUNT_NUMBER_REGEX)
    private String accountNumber;

    @NotNull
    @Pattern(regexp = ValidationConsts.ROUTING_NUMBER_REGEX)
    private String routingNumber;

    private String bizPromotionText;
}
