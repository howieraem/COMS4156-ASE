package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
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
@Service
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
  public ResponseEntity<RequestRsp> getRequestsByUser(User user) {
    // get params
    // payload
    RequestRsp res = new RequestRsp();
    List<Transaction> listTrans = getRequestByUser(user);
    res.setSuccess(listTrans != null);
    res.setCurrBalance(user.getBalance());

    res.setRequests(transactionService.generateListResponseFromTransactions(listTrans));
    res.setMessage("Retrieved user's requests!");
    if (listTrans != null) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    // TODO refactor this?
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @Override
  public List<Transaction> getAllRequests() {
    return transactionService.getAllTransactions();
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
  public ResponseEntity<RequestRsp> createRequest(RequestReq req) {
    // get params
    Long fromUid = req.getFromUid();      // from = req by
    Long toUid = req.getToUid();          // to = req to
    BigDecimal amount = req.getAmount();
    Category category = req.getCategory();
    String desc = req.getDescription();
    // create a request
    User fromUser = userService.getUserById(fromUid);
    User toUser = userService.getUserById(toUid);
    Transaction trans = createRequest(fromUser, toUser, amount, desc, category);
    // payload
    // todo need to refactor some of these
    RequestRsp res = new RequestRsp();
    res.setSuccess(trans != null);
    res.setCurrBalance(fromUser.getBalance());
    res.setRequests(transactionService.generateListResponseFromTransactions(
            new ArrayList<>(Arrays.asList(trans))));
    res.setMessage("Request has been created successfully!");
    if (trans != null) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    // TODO refactor this?
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @Override
  public boolean validateRequest(Transaction request) {
    // TODO maybe it's better to handle exceptions here?
    // TODO validate user?
    // a valid request = valid users, transaction
    // status should be pending, otherwise it's
    // not a valid REQUEST, it's a TRANSFER
    return existsRequestById(request.getId());
  }

  @Override
  public boolean acceptRequest(Transaction request) {
    // here user is reqTo
    if (!validateRequest(request)) {
      return false;
    }
    // maybe need to refactor this
    return transactionService.executeTransaction(request);
  }

  @Override
  public ResponseEntity<RequestRsp> acceptRequest(Long tid, Long fuid, Long tuid) {
    // verify
    boolean valid = existsRequestById(tid)
            && userService.existsById(fuid)
            && userService.existsById(tuid)
            && getRequestById(tid).getFrom().getId().equals(fuid)
            && getRequestById(tid).getTo().getId().equals(tuid)
            && getRequestById(tid).getStatus() == TransactionStatus.TRANS_PENDING;

    // find request
    Transaction trans = getRequestById(tid);
    // accept request
    acceptRequest(trans);
    // payload
    RequestRsp res = new RequestRsp();
    if (!valid) {
      res.setSuccess(false);
      res.setMessage("Invalid params for accepting request.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    res.setSuccess(trans != null);
    res.setCurrBalance(userService.getUserById(fuid).getBalance());
    res.setRequests(transactionService.generateListResponseFromTransactions(
            new ArrayList<>(Arrays.asList(trans))));
    res.setMessage("Successfully accepted request!");
    if (trans != null) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    // TODO refactor this?
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @Override
  public boolean declineRequest(Transaction request) {
    // here user is reqTo
    if (!validateRequest(request)) {
      return false;
    }
    request.setStatus(TransactionStatus.TRANS_DENIED);
    transactionService.saveTransaction(request);
    return true;
  }




  @Override
  public ResponseEntity<RequestRsp> declineRequest(Long tid, Long fuid, Long tuid) {
    // verify
    boolean valid = existsRequestById(tid)
            && userService.existsById(fuid)
            && userService.existsById(tuid)
            && getRequestById(tid).getFrom().getId().equals(fuid)
            && getRequestById(tid).getTo().getId().equals(tuid)
            && getRequestById(tid).getStatus() == TransactionStatus.TRANS_PENDING;

    // find request
    Transaction trans = getRequestById(tid);
    // decline request
    declineRequest(trans);
    // payload
    RequestRsp res = new RequestRsp();
    // todo we should use refactor this into exception handler
    if (!valid) {
      res.setSuccess(false);
      StringBuilder msg = new StringBuilder("Invalid params for declining request. ");
      if (trans.getStatus() == TransactionStatus.TRANS_DENIED) {
        msg.append("Request is already declined.");
      } else if (trans.getStatus() == TransactionStatus.TRANS_COMPLETE) {
        msg.append("Request is already completed. ");
      }
      res.setMessage(msg.toString());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    res.setSuccess(trans != null);
    res.setCurrBalance(userService.getUserById(fuid).getBalance());
    res.setRequests(transactionService.generateListResponseFromTransactions(
            new ArrayList<>(Arrays.asList(trans))));
    res.setMessage("Successfully declined request!");
    if (trans != null) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    // TODO refactor this?
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }
}
