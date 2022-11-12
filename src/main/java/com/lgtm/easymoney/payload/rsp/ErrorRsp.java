package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * error response payload.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ErrorRsp {
  private List<String> errorFields;
  private String message;
}
