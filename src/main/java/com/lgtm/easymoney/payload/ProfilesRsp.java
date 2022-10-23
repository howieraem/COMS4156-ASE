package com.lgtm.easymoney.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * search response, returns the list of matching user profile.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProfilesRsp {
  boolean success;
  private List<ProfileRsp> userProfiles;
}
