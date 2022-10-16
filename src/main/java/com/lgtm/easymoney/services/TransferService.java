package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface TransferService {
    ResponseEntity<TransferRsp> makeATransfer(TransferReq req);
}