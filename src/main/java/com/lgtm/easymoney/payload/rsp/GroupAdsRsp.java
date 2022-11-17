package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * group response payload.
 */
@AllArgsConstructor
@Getter
public class GroupAdsRsp {
  private List<String> ads;
}
