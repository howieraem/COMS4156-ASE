package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response for returning user's profile payload.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProfileRsp {
  private Long uid;
  private String accountName;
  private String email;
  private String address;
  private String phone;
  private UserType userType;
}
