package com.lgtm.easymoney.payload;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * invite to group request payload.
 */
@Data
public class InviteToGroupReq {
  @NotNull
  private Long gid;

  @NotNull
  private Long inviterId;

  @NotNull
  private Long inviteeId;
}
