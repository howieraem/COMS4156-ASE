package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * group response payload.
 */
@Data
@NoArgsConstructor
public class GroupRsp {
  private Long gid;
  private String name;
  private String description;
  private List<Long> uids;
}
