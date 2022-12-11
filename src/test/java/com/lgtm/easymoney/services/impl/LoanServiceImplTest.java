package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.configs.UserTestConfig;
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
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for LoanService.
 * {@link com.lgtm.easymoney.services.impl.LoanServiceImpl}
 */

@RunWith(SpringRunner.class)
public class LoanServiceImplTest {
  @Mock
  private TransactionService transactionService;
  @Mock
  private UserService userService;
  @Mock
  private RequestService requestService;
  @InjectMocks
  private LoanServiceImpl loanService;

  private RequestReq requestReq;
  private User user1;
  private User user2;
  private User user3;
  private Transaction transaction;
  private TransactionRsp transactionRsp;
  private RequestAcceptDeclineReq requestAcceptDeclineReq;
  private Long id1 = 1L;
  private Long id2 = 2L;
  private BigDecimal amount = new BigDecimal(10);
  private Category category = Category.PARTY;
  private String description = "test transfer";
  private Long transactionId = 11L;
  private Long paybackId = 22L;
  private BigDecimal user1Balance = new BigDecimal(50);
  private BigDecimal user2Balance = new BigDecimal(100);
  private Date lastUpdateTime = new Date(20221020L);
  private String message = "User's loans returned";

  /**
   * Set up reusable test fixtures.
   */
  @Before
  public void setUp() {
    // user1
    user1 = new User();
    user1.setId(id1);
    user1.setEmail("a@a.com");
    user1.setPassword("a");
    user1.setBalance(user1Balance);
    user1.setType(UserType.PERSONAL);
    // user2
    user2 = new User();
    user2.setId(id2);
    user2.setEmail("b@b.com");
    user2.setPassword("b");
    user2.setBalance(user2Balance);
    user2.setType(UserType.FINANCIAL);
    // user3
    user3 = UserTestConfig.FIN_USR;

    // requestReq
    requestReq = new RequestReq();
    requestReq.setToUid(id2);
    requestReq.setAmount(amount);
    requestReq.setCategory(String.valueOf(category));
    requestReq.setDescription(description);
    // transaction
    transaction = new Transaction();
    transaction.setFrom(user2);
    transaction.setTo(user1);
    transaction.setAmount(amount);
    transaction.setCategory(category);
    transaction.setDescription(description);
    transaction.setStatus(TransactionStatus.LOAN_PENDING);
    transaction.setId(transactionId);
    // transaction response
    transactionRsp = new TransactionRsp(id2, id1, 1L, amount, TransactionStatus.LOAN_PENDING,
        description, category, lastUpdateTime);
    // requestAcceptDeclineReq
    requestAcceptDeclineReq = new RequestAcceptDeclineReq();
    requestAcceptDeclineReq.setToUid(id1);
    requestAcceptDeclineReq.setRequestid(transactionId);
  }

  @Test
  public void getLoansByUserSuccess() {
    // Arrange
    Mockito.when(transactionService.getAllTransactionsWithUser(any(), any()))
        .thenReturn(List.of(transaction));
    Mockito.when(transactionService.generateListResponseFromTransactions(any()))
        .thenReturn(List.of(transactionRsp));
    LoanRsp expectedResponse = new LoanRsp();
    expectedResponse.setLoans(List.of(transactionRsp));
    expectedResponse.setMessage(message);
    // Act
    LoanRsp returnedResponse = loanService.getLoansByUser(user1);
    // Assert
    assertEquals(returnedResponse, expectedResponse);
  }

  @Test
  public void requestLoanSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(requestReq.getToUid())).thenReturn(user2);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    ResourceCreatedRsp expectedResponse = new ResourceCreatedRsp(transactionId);
    // Act
    ResourceCreatedRsp returnedResponse = loanService.requestLoan(user1, requestReq);
    // Assert
    assertEquals(returnedResponse, expectedResponse);
  }

  @Test
  public void requestLoanFailedWithInvalidBorrowerType() {
    // Arrange
    user1.setType(UserType.BUSINESS);
    Mockito.when(userService.getUserById(requestReq.getToUid())).thenReturn(user2);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.requestLoan(user1, requestReq));
  }

  @Test
  public void requestLoanFailedWithInvalidLenderType() {
    // Arrange
    user2.setType(UserType.PERSONAL);
    Mockito.when(userService.getUserById(requestReq.getToUid())).thenReturn(user2);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.requestLoan(user1, requestReq));
  }

  @Test
  public void approveLoanSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    Mockito.when(transactionService.executeTransaction(any())).thenReturn(true);

    // updated loan
    Transaction loan = new Transaction();
    loan.setFrom(user2);
    loan.setTo(user1);
    loan.setAmount(amount);
    loan.setCategory(category);
    loan.setDescription(description);
    loan.setStatus(TransactionStatus.LOAN_APPROVED);
    loan.setId(transactionId);
    // payback
    Transaction payback = new Transaction();
    payback.setFrom(user1);
    payback.setTo(user2);
    payback.setAmount(amount);
    payback.setCategory(Category.LOAN_PAYBACK);
    payback.setDescription(String.valueOf(transactionId));
    payback.setStatus(TransactionStatus.TRANS_PENDING);
    payback.setId(paybackId);
    // payback response
    TransactionRsp paybackRsp = new TransactionRsp(id1, id2, 2L, amount,
        TransactionStatus.TRANS_PENDING, String.valueOf(transactionId), Category.LOAN_PAYBACK,
        lastUpdateTime);

    Mockito.when(transactionService.saveTransaction(any())).thenReturn(loan);
    Mockito.when(requestService.createRequest(user2, user1, amount, String.valueOf(transactionId),
        Category.LOAN_PAYBACK)).thenReturn(payback);
    TransactionRsp loanRsp = new TransactionRsp(
        id2, id1, 3L, amount, TransactionStatus.LOAN_APPROVED,
        description, category, lastUpdateTime);
    Mockito.when(transactionService.generateListResponseFromTransactions(
        List.of(loan, payback))).thenReturn(List.of(loanRsp, paybackRsp));
    LoanRsp expectedResponse = new LoanRsp();
    expectedResponse.setMessage("User's approved loan and required payback request returned");
    expectedResponse.setLoans(List.of(loanRsp, paybackRsp));
    // Act
    LoanRsp returnedResponse = loanService.approveLoan(user2, requestAcceptDeclineReq);
    // Assert
    assertEquals(returnedResponse, expectedResponse);
  }

  @Test
  public void approveLoanFailedWithInvalidBorrowerType() {
    // Arrange
    user1.setType(UserType.BUSINESS);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.approveLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void approveLoanFailedWithInvalidLenderType() {
    // Arrange
    user2.setType(UserType.PERSONAL);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.approveLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void approveLoanFailedWithWrongLender() {
    // Arrange
    Mockito.when(userService.getUserById(user3.getId())).thenReturn(user3);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.approveLoan(user3, requestAcceptDeclineReq));
  }

  @Test
  public void approveLoanFailedWithWrongToUid() {
    // Arrange
    requestAcceptDeclineReq.setToUid(3L);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.approveLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void approveLoanFailedWithWrongLoanStatus() {
    // Arrange
    transaction.setStatus(TransactionStatus.LOAN_APPROVED);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.approveLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void approveLoanFailedWithUnsuccessfulTransactionExecution() {
    // Arrange
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    Mockito.when(transactionService.executeTransaction(any())).thenReturn(false);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.approveLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void declineLoanSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);

    // updated loan
    Transaction loan = new Transaction();
    loan.setFrom(user2);
    loan.setTo(user1);
    loan.setAmount(amount);
    loan.setCategory(category);
    loan.setDescription(description);
    loan.setStatus(TransactionStatus.LOAN_DECLINED);
    loan.setId(transactionId);

    Mockito.when(transactionService.saveTransaction(any())).thenReturn(loan);
    TransactionRsp loanRsp = new TransactionRsp(
        id2, id1, 1L, amount, TransactionStatus.LOAN_DECLINED,
        description, category, lastUpdateTime);
    Mockito.when(transactionService.generateListResponseFromTransactions(
        List.of(loan))).thenReturn(List.of(loanRsp));
    LoanRsp expectedResponse = new LoanRsp();
    expectedResponse.setMessage("Declined loan returned");
    expectedResponse.setLoans(List.of(loanRsp));
    // Act
    LoanRsp returnedResponse = loanService.declineLoan(user2, requestAcceptDeclineReq);
    // Assert
    assertEquals(returnedResponse, expectedResponse);
  }

  @Test
  public void declineLoanFailedWithInvalidBorrowerType() {
    // Arrange
    user1.setType(UserType.BUSINESS);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.declineLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void declineLoanFailedWithInvalidLenderType() {
    // Arrange
    user2.setType(UserType.PERSONAL);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> loanService.declineLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void declineLoanFailedWithWrongLender() {
    // Arrange
    Mockito.when(userService.getUserById(user3.getId())).thenReturn(user3);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.declineLoan(user3, requestAcceptDeclineReq));
  }

  @Test
  public void declineLoanFailedWithWrongToUid() {
    // Arrange
    requestAcceptDeclineReq.setToUid(3L);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.declineLoan(user2, requestAcceptDeclineReq));
  }

  @Test
  public void declineLoanFailedWithWrongLoanStatus() {
    // Arrange
    transaction.setStatus(TransactionStatus.LOAN_DECLINED);
    Mockito.when(userService.getUserById(requestAcceptDeclineReq.getToUid())).thenReturn(user1);
    Mockito.when(transactionService.getTransactionById(requestAcceptDeclineReq.getRequestid()))
        .thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> loanService.declineLoan(user2, requestAcceptDeclineReq));
  }
}