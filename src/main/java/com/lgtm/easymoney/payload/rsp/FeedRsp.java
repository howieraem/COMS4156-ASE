package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Feed response, returns end user's activity + friends' activity in chronological order
 * NOTE modified TransactionRsp to include promo text.
 */
@AllArgsConstructor
@Getter
public class FeedRsp {
  private List<FeedActivityRsp> activities;
}
