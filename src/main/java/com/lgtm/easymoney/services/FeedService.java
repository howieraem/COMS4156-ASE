package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.FeedActivityRsp;
import com.lgtm.easymoney.payload.rsp.FeedRsp;
import java.util.List;

/**
 * Feed service interface. restful api for feed,
 */
public interface FeedService {
  FeedRsp getFeed(User u); // external
}
