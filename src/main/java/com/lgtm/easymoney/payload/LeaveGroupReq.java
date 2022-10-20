package com.lgtm.easymoney.payload;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * leave group request payload.
 */
@Data
public class LeaveGroupReq {
  @NotNull
  private Long uid;

  @NotNull
  private Long gid;
}
