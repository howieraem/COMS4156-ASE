package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * error response payload.
 */
@AllArgsConstructor
@Getter
public class ErrorRsp {
  private List<String> errorFields;
  private String message;
}
