package com.lgtm.easymoney.payload;

import javax.validation.constraints.NotNull;
import lombok.Data;


@Data
public class FriendshipReq {
  @NotNull
  private Long uid1;

  @NotNull
  private Long uid2;

  private String message;
}
