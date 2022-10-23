package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.models.User;
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

  /**
   * Construct a user profile from a user object.
   *
   * @param u A user object
   */
  public ProfileRsp(User u) {
    this.uid = u.getId();
    this.accountName = u.getAccount().getAccountName();
    this.email = u.getEmail();
    this.address = u.getAddress();
    this.phone = u.getPhone();
    this.userType = u.getType();
  }
}
