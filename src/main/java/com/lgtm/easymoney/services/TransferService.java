package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import org.springframework.http.ResponseEntity;

/**
 * transfer service interface.
 */
public interface TransferService {
  ResourceCreatedRsp makeTransfer(TransferReq req);

  TransferRsp getTransfersByUid(Long uid);
}
