package com.lgtm.easymoney.payload.req;

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
  private Long inviteeId;
}
