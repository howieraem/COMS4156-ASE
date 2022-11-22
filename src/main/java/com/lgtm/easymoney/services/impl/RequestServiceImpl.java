package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.RequestRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * request service implementation.
 */
@Service("requestService")
public class RequestServiceImpl implements RequestService {
  private final TransactionService transactionService;
  private final UserService userService;

  private static final String COMMON_ERR_FIELD = "requestId";

  // initialize service with dependent services:
  // userService, transactionService
  @Autowired
  public RequestServiceImpl(TransactionService transactionService, UserService userService) {
    this.transactionService = transactionService;
    this.userService = userService;
  }

  /**
   * checks if a request exists given id.
   *
   * @param id request id
   * @return boolean
   */
  @Override
  public boolean existsRequestById(Long id) {
    return transactionService.existsTransactionById(id);
  }

  /**
   * get request(transaction) by rid.
   *
   * @param id request id
   * @return Transaction
   */
  @Override
  public Transaction getRequestById(Long id) {
    return transactionService.getTransactionById(id);
  }

  /**
   * save a request to DB.
   *
   * @param trans transaction
   * @return saved transaction
   */
  @Override
  public Transaction saveRequest(Transaction trans) {
    return transactionService.saveTransaction(trans);
  }

  /**
   * get list of requests that are either sent/received by user.
   *
   * @param user user
   * @return list of transactions
   */
  @Override
  public List<Transaction> getRequestByUser(User user) {
    List<TransactionStatus> status =
            List.of(TransactionStatus.TRANS_PENDING, TransactionStatus.TRANS_DENIED);
    return transactionService.getAllTransactionsWithUser(user, status);
  }

  /**
   * EXTERNAL generate a response payload of user's requests.
   *
   * @param user current logged-in user
   * @return response payload with list of transactions
   */
  @Override
  public RequestRsp getRequests(User user) {
    RequestRsp res = new RequestRsp();
    List<Transaction> listTrans = getRequestByUser(user);
    res.setSuccess(true);
    res.setCurrBalance(user.getBalance());

    res.setRequests(transactionService.generateListResponseFromTransactions(listTrans));
    res.setMessage("Retrieved user's requests!");
    return res;
  }

  /**
   * create a transation representing a request with details.
   *
   * @param reqBy user who sent req
   * @param reqTo user the req is targeting
   * @param amount amount of money
   * @param desc a short description of the req
   * @param category category of the expense
   * @return transaction
   */
  @Override
  public Transaction createRequest(User reqBy, User reqTo, BigDecimal amount, String desc,
                                   Category category) {
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

  /**
   * EXTERNAL create a request.
   *
   * @param requester current logged-in user
   * @param req request payload
   * @return payload with id of request created.
   */
  @Override
  public ResourceCreatedRsp createRequest(User requester, RequestReq req) {
    // create a request
    Transaction trans = createRequest(
            requester,
            userService.getUserById(req.getToUid()),
            req.getAmount(),
            req.getDescription(),
            Category.valueOf(req.getCategory().toUpperCase()));
    // response
    return new ResourceCreatedRsp(trans.getId());
  }

  /**
   * check if an accept is valid to be accepted/declined.
   * NOTE that transactions that are already completed/denied
   * cannot be modified.
   *
   * @param tid transaction id
   * @param fuid from user uid
   * @param tuid to user uid
   * @return boolean, can accept/decline or not
   */
  @Override
  public boolean canAcceptDeclineRequest(Long tid, Long fuid, Long tuid) {
    Transaction t = getRequestById(tid);
    return t.getFrom().getId().equals(fuid)
            && t.getTo().getId().equals(tuid)
            && t.getStatus() == TransactionStatus.TRANS_PENDING;
  }

  /**
   * accept request by changing the status to be COMPLETED.
   *
   * @param request transaction to be completed
   * @return boolean, successful or not
   */
  @Override
  public boolean acceptRequest(Transaction request) {
    return transactionService.executeTransaction(request);
  }

  /**
   * EXTERNAL accept a request given request id, from user id
   * and to user id.
   *
   * @param tid transaction id
   * @param fuid from user id
   * @param tuid to user id
   * @return payload containing id of request accepted
   */
  @Override
  public ResourceCreatedRsp acceptRequest(Long tid, Long fuid, Long tuid) {
    // verify
    if (!canAcceptDeclineRequest(tid, fuid, tuid)) {
      // status not pending or info not matched
      throw new InvalidUpdateException("Transaction", tid, COMMON_ERR_FIELD, tid);
    }
    // accept request
    if (!acceptRequest(getRequestById(tid))) {
      // not enough balance
      throw new InvalidUpdateException("User", fuid, COMMON_ERR_FIELD, tid);
    }
    // response
    return new ResourceCreatedRsp(tid);

  }

  /**
   * INTERNAL decline a request by marking its status as DENIED.
   *
   * @param request request/transaction to be declined
   * @return TRUE, susccessfully declined
   */
  @Override
  public boolean declineRequest(Transaction request) {
    request.setStatus(TransactionStatus.TRANS_DENIED);
    transactionService.saveTransaction(request);
    return true;
  }

  /**
   * EXTERNAL decline a request given request id, from user id
   * and to user id.
   *
   * @param tid transaction id
   * @param fuid from user id
   * @param tuid to user id
   * @return payload containing id of request declined.
   */
  @Override
  public ResourceCreatedRsp declineRequest(Long tid, Long fuid, Long tuid) {
    // verify
    if (!canAcceptDeclineRequest(tid, fuid, tuid)) {
      // status not pending or info not matched
      throw new InvalidUpdateException("Transaction", tid, COMMON_ERR_FIELD, tid);
    }
    // decline request
    declineRequest(getRequestById(tid));
    // response
    return new ResourceCreatedRsp(tid);

  }
}
