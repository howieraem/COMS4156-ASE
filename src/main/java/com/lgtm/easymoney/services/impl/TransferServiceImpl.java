package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.TransferService;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {
    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransferServiceImpl(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    private boolean makeATransfer(User fromUser, User toUser, BigDecimal amount, Category category, String desc) {
        Transaction transaction = new Transaction();
        transaction.setFrom(fromUser);
        transaction.setTo(toUser);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(desc);
        transaction.setStatus(TransactionStatus.REQ_PENDING);
        return transactionService.executeTransaction(transaction);
    }

    @Override
    public ResponseEntity<TransferRsp> makeATransfer(TransferReq req) {
        // get params
        Long fromUid = req.getFromUid();
        Long toUid = req.getToUid();
        BigDecimal amount = req.getAmount();
        Category category = req.getCategory();
        String desc = req.getDescription();
        // account validation is currently eliminated because account is guaranteed to exist
        // make a transfer
        User fromUser = userService.getUserByID(fromUid);
        User toUser = userService.getUserByID(toUid);
        boolean success = makeATransfer(fromUser, toUser, amount, category, desc);
        // payload
        TransferRsp response = new TransferRsp();
        response.setSuccess(success);
        response.setCurrBalance(fromUser.getBalance());
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}