package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import org.springframework.http.ResponseEntity;

/**
 * transfer service interface.
 */
public interface TransferService {
  ResponseEntity<TransferRsp> makeTransfer(TransferReq req);

  ResponseEntity<TransferRsp> getTransfersByUid(Long uid);
}
