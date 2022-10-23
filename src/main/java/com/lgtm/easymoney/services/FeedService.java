package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.FeedActivityRsp;
import com.lgtm.easymoney.payload.FeedRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import java.math.BigDecimal;
import java.util.List;

/**
 * Feed service interface. restful api for feed,
 */
public interface FeedService {
  List<FeedActivityRsp> getFeedByUser(User u);  // internal

  FeedRsp getFeedByUid(Long uid); // external
}
