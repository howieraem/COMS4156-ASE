package com.lgtm.easymoney.payload;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    @Size(min = 10, max = 10)
    private String phone;

    @NotBlank
    private String accountName;

    /* Regarding account number lengths: https://qr.ae/pvJXyd */
    @NotBlank
    @Size(min = 1, max = 17)
    private String accountNumber;
    @NotBlank
    @Size(min = 9, max = 9)
    private String routingNumber;
}
