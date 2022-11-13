package com.lgtm.easymoney.services;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.RequestRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import java.math.BigDecimal;
import java.util.List;

/**
 * request service interface.
 */
public interface RequestService {
  boolean existsRequestById(Long id);

  Transaction getRequestById(Long id);

  Transaction saveRequest(Transaction trans);

  List<Transaction> getRequestByUser(User user);

  Transaction createRequest(
          User reqBy, User reqTo, BigDecimal amount, String desc, Category category);

  ResourceCreatedRsp createRequest(User requester, RequestReq req);

  boolean canAcceptDeclineRequest(Long tid, Long fuid, Long tuid);

  boolean acceptRequest(Transaction request);

  ResourceCreatedRsp acceptRequest(Long tid, Long fuid, Long tuid);

  boolean declineRequest(Transaction request);

  ResourceCreatedRsp declineRequest(Long tid, Long fuid, Long tuid);

  RequestRsp getRequests(User user);
}
