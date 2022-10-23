package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.LoanRsp;
import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.services.LoanService;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Loan service implementation.
 */
@Service("loanService")
public class LoanServiceImpl implements LoanService {
  private final TransactionService transactionService;
  private final UserService userService;
  private final RequestService requestService;

  /**
   * Loan service implementation.
   */
  @Autowired
  public LoanServiceImpl(
      TransactionService transactionService,
      UserService userService,
      RequestService requestService) {
    this.transactionService = transactionService;
    this.userService = userService;
    this.requestService = requestService;
  }

  private void validateLoanUsers(User borrower, User lender) {
    if (!lender.getType().equals(UserType.FINANCIAL)) {
      throw new InapplicableOperationException("user", lender.getId(), "toUid", "requestLoan");
    }
    if (!borrower.getType().equals(UserType.PERSONAL)) {
      throw new InapplicableOperationException("user", borrower.getId(), "fromUid", "requestLoan");
    }
  }

  private Transaction createLoanRequest(User reqBy, User reqTo, BigDecimal amount, String desc,
      Category category) {
    Transaction trans = new Transaction();
    // request: requested by A, requested to B
    // once accept, money goes from B to A
    trans.setFrom(reqTo);
    trans.setTo(reqBy);
    trans.setAmount(amount);
    trans.setDescription(desc);
    trans.setCategory(category);
    trans.setStatus(TransactionStatus.LOAN_PENDING);
    return transactionService.saveTransaction(trans);
  }

  private List<Transaction> getLoansByUser(User user) {
    List<TransactionStatus> status = List.of(
        TransactionStatus.LOAN_PENDING,
        TransactionStatus.LOAN_APPROVED,
        TransactionStatus.LOAN_DECLINED);
    return transactionService.getAllTransactionsWithUser(user, status);
  }

  private Transaction getLoanById(Long id) {
    return transactionService.getTransactionById(id);
  }

  private boolean validateLoanRequest(Long lid, Long fromUid, Long toUid) {
    Transaction t = getLoanById(lid);
    return t.getFrom().getId().equals(fromUid)
        && t.getTo().getId().equals(toUid)
        && t.getStatus() == TransactionStatus.LOAN_PENDING;
  }

  @Override
  public ResourceCreatedRsp requestLoan(RequestReq req) {
    // get params
    BigDecimal amount = req.getAmount();
    String category = req.getCategory();
    String desc = req.getDescription();
    User borrower = userService.getUserById(req.getFromUid());
    User lender = userService.getUserById(req.getToUid());
    // validate if loan user type is valid
    validateLoanUsers(borrower, lender);
    // create loan request
    Transaction loan = createLoanRequest(borrower, lender, amount, desc,
        Category.valueOf(category.toUpperCase()));
    // response
    return loan == null ? new ResourceCreatedRsp(null) : new ResourceCreatedRsp(loan.getId());
  }

  @Override
  public LoanRsp getLoansByUid(Long uid) {
    User user = userService.getUserById(uid);
    List<Transaction> loans = getLoansByUser(user);
    // response
    LoanRsp response = new LoanRsp();
    response.setSuccess(true);
    List<TransactionRsp> transferRsps =
        transactionService.generateListResponseFromTransactions(loans);
    response.setLoans(transferRsps);
    response.setMessage("User's loans returned");
    return response;
  }

  private boolean approveLoan(Transaction loan) {
    loan.setStatus(TransactionStatus.TRANS_PENDING);
    transactionService.saveTransaction(loan);
    boolean success = transactionService.executeTransaction(loan);
    if (success) {
      loan.setStatus(TransactionStatus.LOAN_APPROVED);
      transactionService.saveTransaction(loan);
    } else {
      loan.setStatus(TransactionStatus.LOAN_PENDING);
      transactionService.saveTransaction(loan);
    }
    return success;
  }

  @Override
  public LoanRsp approveLoan(RequestAcceptDeclineReq req) {
    // get params
    Long fromUid = req.getFromUid();
    Long toUid = req.getToUid();
    Long lid = req.getRequestid();
    // validate
    User lender = userService.getUserById(fromUid);
    User borrower = userService.getUserById(toUid);
    validateLoanUsers(borrower, lender);
    if (!validateLoanRequest(lid, fromUid, toUid)) {
      throw new InvalidUpdateException("approve loan", lid, "loanId", lid);
    }
    // approve loan and transfer money
    Transaction loan = getLoanById(lid);
    boolean success = approveLoan(loan);
    if (!success) {
      throw new InvalidUpdateException("approve loan", loan.getId(),
          "loan id", loan.getId());
    }
    // create a loan-payback request from lender to borrower, storing loanId as description
    Transaction payback = requestService.createRequest(lender, borrower, loan.getAmount(),
        String.valueOf(loan.getId()), Category.LOAN_PAYBACK);
    // response returns the approved loan and the loan-payback request
    LoanRsp response = new LoanRsp();
    response.setSuccess(true);
    List<TransactionRsp> transferRsps =
        transactionService.generateListResponseFromTransactions(List.of(loan, payback));
    response.setLoans(transferRsps);
    response.setMessage("User's approved loan and required payback request returned");
    return response;
  }

  private boolean declineLoan(Transaction loan) {
    loan.setStatus(TransactionStatus.LOAN_DECLINED);
    transactionService.saveTransaction(loan);
    return true;
  }

  @Override
  public ResourceCreatedRsp declineLoan(RequestAcceptDeclineReq req) {
    // get params
    Long fromUid = req.getFromUid();
    Long toUid = req.getToUid();
    Long lid = req.getRequestid();
    // validate
    User lender = userService.getUserById(fromUid);
    User borrower = userService.getUserById(toUid);
    validateLoanUsers(borrower, lender);
    if (!validateLoanRequest(lid, fromUid, toUid)) {
      throw new InvalidUpdateException("decline loan", lid, "loanId", lid);
    }
    // decline loan
    declineLoan(getLoanById(lid));
    // response
    return new ResourceCreatedRsp(lid);
  }
}