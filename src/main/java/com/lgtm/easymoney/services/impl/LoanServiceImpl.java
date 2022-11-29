package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
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

  /**
   * Verify that the borrower is personal user and the lender is financial user.
   */
  private void validateLoanUsers(User borrower, User lender) {
    if (!lender.getType().equals(UserType.FINANCIAL)) {
      throw new InapplicableOperationException("user", lender.getId(), "toUid", "loan");
    }
    if (!borrower.getType().equals(UserType.PERSONAL)) {
      throw new InapplicableOperationException("user", borrower.getId(), "fromUid", "loan");
    }
  }

  /**
   * Create a loan transaction object and save it to database.
   *
   * @param reqBy the user that borrows the loan
   * @param reqTo the user that lends the loan
   * @param amount the amount of loan
   * @param desc description
   * @param category the category that the loan is used for
   * @return the created transaction object
   */
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

  /**
   * Get a list of loans by user.
   */
  @Override
  public LoanRsp getLoansByUser(User user) {
    List<TransactionStatus> status = List.of(
        TransactionStatus.LOAN_PENDING,
        TransactionStatus.LOAN_APPROVED,
        TransactionStatus.LOAN_DECLINED);
    var loans = transactionService.getAllTransactionsWithUser(user, status);
    LoanRsp response = new LoanRsp();
    List<TransactionRsp> transferRsps =
        transactionService.generateListResponseFromTransactions(loans);
    response.setLoans(transferRsps);
    response.setMessage("User's loans returned");
    return response;
  }

  /**
   * Get loan by loan id from database.
   */
  private Transaction getLoanById(Long id) {
    return transactionService.getTransactionById(id);
  }

  /**
   * Verify that the loan request has valid lender, borrower, and LOAN_PENDING status.
   */
  private boolean validateLoanRequest(Long lid, Long fromUid, Long toUid) {
    Transaction t = getLoanById(lid);
    return t.getFrom().getId().equals(fromUid)
        && t.getTo().getId().equals(toUid)
        && t.getStatus() == TransactionStatus.LOAN_PENDING;
  }

  /**
   * Create a loan request.
   *
   * @param borrower current logged-in lender.
   * @param req loan request.
   * @return resource created response containing created loan id
   */
  @Override
  public ResourceCreatedRsp requestLoan(User borrower, RequestReq req) {
    // get params
    BigDecimal amount = req.getAmount();
    String category = req.getCategory();
    String desc = req.getDescription();
    User lender = userService.getUserById(req.getToUid());
    // validate if loan user type is valid
    validateLoanUsers(borrower, lender);
    // create loan request
    Transaction loan = createLoanRequest(borrower, lender, amount, desc,
        Category.valueOf(category.toUpperCase()));
    // response
    return new ResourceCreatedRsp(loan.getId());
  }

  /**
   * Execute the loan transaction and update the status of approved line loan transaction in
   * database.
   */
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

  /**
   * Approve a loan request.
   *
   * @param lender current logged-in lender.
   * @param req request payload, contains loan id, lender id, borrower id.
   * @return loan response
   */
  @Override
  public LoanRsp approveLoan(User lender, RequestAcceptDeclineReq req) {
    // get params
    Long fromUid = lender.getId();
    Long toUid = req.getToUid();
    Long lid = req.getRequestid();
    // validate
    User borrower = userService.getUserById(toUid);
    validateLoanUsers(borrower, lender);
    if (!validateLoanRequest(lid, fromUid, toUid)) {
      throw new InvalidUpdateException("loan", lid, "loanId", lid);
    }
    // approve loan and transfer money
    Transaction loan = getLoanById(lid);
    boolean success = approveLoan(loan);
    if (!success) {
      throw new InvalidUpdateException("loan", loan.getId(),
          "loan id", loan.getId());
    }
    // create a loan-payback request from lender to borrower, storing loanId as description
    Transaction payback = requestService.createRequest(lender, borrower, loan.getAmount(),
        String.valueOf(loan.getId()), Category.LOAN_PAYBACK);
    // response returns the approved loan and the loan-payback request
    LoanRsp response = new LoanRsp();
    List<TransactionRsp> transferRsps =
        transactionService.generateListResponseFromTransactions(List.of(loan, payback));
    response.setLoans(transferRsps);
    response.setMessage("User's approved loan and required payback request returned");
    return response;
  }

  /**
   * Update the status of decline loan transaction in database.
   */
  private Transaction declineLoan(Transaction loan) {
    loan.setStatus(TransactionStatus.LOAN_DECLINED);
    return transactionService.saveTransaction(loan);
  }

  /**
   * Decline a loan request.
   *
   * @param lender current logged-in lender.
   * @param req request payload, contains loan id, lender id, borrower id.
   * @return loan response
   */
  @Override
  public LoanRsp declineLoan(User lender, RequestAcceptDeclineReq req) {
    // get params
    Long fromUid = lender.getId();
    Long toUid = req.getToUid();
    Long lid = req.getRequestid();
    // validate
    User borrower = userService.getUserById(toUid);
    validateLoanUsers(borrower, lender);
    if (!validateLoanRequest(lid, fromUid, toUid)) {
      throw new InvalidUpdateException("loan", lid, "loanId", lid);
    }
    // decline loan
    Transaction loan = declineLoan(getLoanById(lid));
    // response
    LoanRsp response = new LoanRsp();
    List<TransactionRsp> transferRsps =
        transactionService.generateListResponseFromTransactions(List.of(loan));
    response.setLoans(transferRsps);
    response.setMessage("Declined loan returned");
    return response;
  }
}