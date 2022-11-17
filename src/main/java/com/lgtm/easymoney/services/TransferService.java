package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;

/**
 * transfer service interface.
 */
public interface TransferService {
  ResourceCreatedRsp makeTransfer(User fromUser, TransferReq req);

  TransferRsp getTransfers(User user);
}
