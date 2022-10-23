package com.lgtm.easymoney.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feed response, returns end user's activity + friends' activity in chronological order
 * NOTE modified TransactionRsp to include promo text.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class FeedRsp {
  private List<FeedActivityRsp> activities;
}
