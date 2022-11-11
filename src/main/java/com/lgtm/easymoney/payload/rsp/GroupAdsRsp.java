package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * group response payload.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class GroupAdsRsp {
  private List<String> ads;
}
