package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;

/**
 * transfer service interface.
 */
public interface TransferService {
  ResourceCreatedRsp makeTransfer(TransferReq req);

  TransferRsp getTransfersByUid(Long uid);
}
