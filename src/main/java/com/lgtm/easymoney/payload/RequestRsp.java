package com.lgtm.easymoney.payload;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response for making a request.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestRsp {
  /* request response
  CREATE a new request:
      param: request details
      return: request
  GET all requests by user:
      param: uid, from/to
      return: list of requests
   ACCEPT a request:
      param: from uid, to uid, request id
      return: request
   DECLINE a request:
      param: from uid, to uid, request id
      return: request
   */
  private Boolean success;
  private String message;
  private List<TransactionRsp> requests;
  private BigDecimal currBalance;
}
