package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * request service implementation.
 */
@Service("requestService")
public class RequestServiceImpl implements RequestService {
  private final TransactionService transactionService;
  private final UserService userService;

  @Autowired
  public RequestServiceImpl(TransactionService transactionService, UserService userService) {
    this.transactionService = transactionService;
    this.userService = userService;
  }

  @Override
  public boolean existsRequestById(Long id) {
    return transactionService.existsTransactionById(id);
  }

  @Override
  public Transaction getRequestById(Long id) {
    return transactionService.getTransactionById(id);
  }

  @Override
  public Transaction saveRequest(Transaction trans) {
    return transactionService.saveTransaction(trans);
  }

  @Override
  public List<Transaction> getRequestByUser(User user) {
    List<TransactionStatus> status =
            List.of(TransactionStatus.TRANS_PENDING, TransactionStatus.TRANS_DENIED);
    return transactionService.getAllTransactionsWithUser(user, status);
  }

  @Override
  public RequestRsp getRequestsByUid(Long uid) {
    User user = userService.getUserById(uid);
    RequestRsp res = new RequestRsp();
    List<Transaction> listTrans = getRequestByUser(user);
    res.setSuccess(listTrans != null);
    res.setCurrBalance(user.getBalance());

    res.setRequests(transactionService.generateListResponseFromTransactions(listTrans));
    res.setMessage("Retrieved user's requests!");
    return res;
  }

  @Override
  public Transaction createRequest(User reqBy, User reqTo, BigDecimal amount, String desc,
                                   Category category) {
    // TODO validate input

    Transaction trans = new Transaction();
    // request: requested by A, requested to B
    // once accept, money goes from B to A
    trans.setFrom(reqTo);
    trans.setTo(reqBy);
    trans.setAmount(amount);
    trans.setDescription(desc);
    trans.setCategory(category);
    trans.setStatus(TransactionStatus.TRANS_PENDING);

    return transactionService.saveTransaction(trans);
  }

  @Override
  public ResourceCreatedRsp createRequest(RequestReq req) {
    // create a request
    Transaction trans = createRequest(
            userService.getUserById(req.getFromUid()),
            userService.getUserById(req.getToUid()),
            req.getAmount(),
            req.getDescription(),
            Category.valueOf(req.getCategory().toUpperCase()));
    // response
    return new ResourceCreatedRsp(trans.getId());
  }

  @Override
  public boolean canAcceptDeclineRequest(Long tid, Long fuid, Long tuid) {
    Transaction t = getRequestById(tid);
    return t.getFrom().getId().equals(fuid)
            && t.getTo().getId().equals(tuid)
            && t.getStatus() == TransactionStatus.TRANS_PENDING;
  }

  @Override
  public boolean acceptRequest(Transaction request) {
    return transactionService.executeTransaction(request);
  }

  @Override
  public ResourceCreatedRsp acceptRequest(Long tid, Long fuid, Long tuid) {
    // verify
    if (!canAcceptDeclineRequest(tid, fuid, tuid)) {
      // todo write a own exception handler
      throw new InvalidUpdateException("accept request", tid, "requestId", tid);
    }
    // accept request
    if (!acceptRequest(getRequestById(tid))) {
      throw new InvalidUpdateException("accept request", tid, "requestId", tid);
    }
    // response
    return new ResourceCreatedRsp(tid);

  }

  @Override
  public boolean declineRequest(Transaction request) {
    // todo refactor to return transaction? or false case
    request.setStatus(TransactionStatus.TRANS_DENIED);
    transactionService.saveTransaction(request);
    return true;
  }

  @Override
  public ResourceCreatedRsp declineRequest(Long tid, Long fuid, Long tuid) {
    // verify
    if (!canAcceptDeclineRequest(tid, fuid, tuid)) {
      // todo write a own exception handler
      throw new InvalidUpdateException("decline request", tid, "requestId", tid);
    }
    // decline request
    declineRequest(getRequestById(tid));
    // response
    return new ResourceCreatedRsp(tid);

  }
}
