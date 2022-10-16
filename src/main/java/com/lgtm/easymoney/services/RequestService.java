package com.lgtm.easymoney.services;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

public interface RequestService {
    boolean existsRequestByID(Long id);
    Transaction getRequestByID(Long id);
    Transaction saveRequest(Transaction trans);
    List<Transaction> getRequestByUser(User user);
    List<Transaction> getAllRequests();
    Transaction createARequest(User reqBy, User reqTo, BigDecimal amount, String desc, Category category);
    boolean validateRequest(Transaction request);
    boolean acceptRequest(Transaction request);
    ResponseEntity<RequestRsp> acceptRequest(Long tid, Long fUid, Long tUid);
    boolean declineRequest(Transaction request);
    ResponseEntity<RequestRsp> declineRequest(Long tid, Long fUid, Long tUid);
    // below are internal APIs
    ResponseEntity<RequestRsp> createARequest(RequestReq req);
    ResponseEntity<RequestRsp> getRequestsByUser(User user);
    // TODO: request response
}
