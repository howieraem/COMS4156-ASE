package com.lgtm.easymoney.payload.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * response for resource being created. returns id of resource.
 */
@AllArgsConstructor
@Data
public class ResourceCreatedRsp {
  private Long id;
}
