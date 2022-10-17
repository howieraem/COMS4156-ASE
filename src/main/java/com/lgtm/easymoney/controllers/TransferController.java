package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import com.lgtm.easymoney.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/create")
    public ResponseEntity<TransferRsp> transfer(@Valid @RequestBody TransferReq req) {
        return transferService.makeATransfer(req);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<TransferRsp> getTransfers(@PathVariable(value="uid") Long uid) {
        // get all the transfers (both from and to) corresponding to the user with given uid
        // TODO: isFromOtTo may be added to param as filter
        return transferService.getTransfersByUid(uid);
    }
}
