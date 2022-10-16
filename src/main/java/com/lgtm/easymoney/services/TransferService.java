package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

public interface TransferService {
    ResponseEntity<TransferRsp> makeATransfer(TransferReq req);
    ResponseEntity<TransferRsp> getTransfersByUid(Long uid);
}