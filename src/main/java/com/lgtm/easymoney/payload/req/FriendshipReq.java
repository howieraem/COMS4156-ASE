package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotNull;
import lombok.Data;

/** Payload for friendship-related client requests. */
@Data
public class FriendshipReq {
  @NotNull
  private Long uid;

  private String note;  // like alias
}
