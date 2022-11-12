package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * leave group request payload.
 */
@Data
public class LeaveGroupReq {
  @NotNull
  private Long gid;
}
