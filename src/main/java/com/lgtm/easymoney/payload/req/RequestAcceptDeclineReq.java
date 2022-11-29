package com.lgtm.easymoney.payload.req;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * payload for handling accpet/decline a payment request.
 */
@Data
public class RequestAcceptDeclineReq {
  @NotNull
  private Long toUid;
  @NotNull
  private Long requestid;
}
