package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.RequestRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for RequestService.
 * {@link com.lgtm.easymoney.services.impl.RequestServiceImpl}
 * */
@RunWith(SpringRunner.class)
public class RequestServiceImplTest {
  @Mock
  private TransactionService transactionService;
  @Mock
  private UserService userService;
  @InjectMocks
  private TransferServiceImpl transferService;
  @InjectMocks
  private RequestServiceImpl requestService;
  private TransferReq transferReq;
  private RequestReq requestReq;
  private User user1;
  private User user2;
  private Transaction transaction;
  private Long id1 = 1L;
  private Long id2 = 2L;
  private BigDecimal amount = new BigDecimal(10);
  private Category category = Category.PARTY;
  private String description = "test transfer";
  private Long transactionId = 11L;
  private BigDecimal user1Balance = new BigDecimal(50);
  private BigDecimal user2Balance = new BigDecimal(100);
  private Date lastUpdateTime = new Date(20221020L);
  private String message = "Retrieved user's requests!";

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // requestReq
    requestReq = new RequestReq();
    requestReq.setToUid(id2);
    requestReq.setAmount(amount);
    requestReq.setCategory(String.valueOf(category));
    requestReq.setDescription(description);
    // user1
    user1 = new User();
    user1.setId(id1);
    user1.setEmail("a@a.com");
    user1.setPassword("a");
    user1.setBalance(user1Balance);
    // user2
    user2 = new User();
    user2.setId(id2);
    user2.setEmail("b@b.com");
    user2.setPassword("b");
    user2.setBalance(user2Balance);
    // transaction
    transaction = new Transaction();
    transaction.setFrom(user1);
    transaction.setTo(user2);
    transaction.setAmount(amount);
    transaction.setCategory(category);
    transaction.setDescription(description);
    transaction.setStatus(TransactionStatus.TRANS_PENDING);
    transaction.setId(transactionId);
  }

  @AfterEach
  public void tearDown() {
    // reset status to prevent pollution from accept/decline testing
    transaction.setStatus(TransactionStatus.TRANS_PENDING);
  }

  @Test
  public void createRequestSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    Mockito.when(userService.getUserById(id2)).thenReturn(user2);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    ResourceCreatedRsp expectedRsp = new ResourceCreatedRsp(transactionId);

    // Act
    ResourceCreatedRsp returnedRsp = requestService.createRequest(user1, requestReq);

    // Assert
    assertEquals(returnedRsp, expectedRsp);
  }

  @Test
  public void getRequestsByUidSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    transaction.setStatus(TransactionStatus.TRANS_PENDING);
    Mockito.when(transactionService.getAllTransactionsWithUser(any(), any()))
            .thenReturn(List.of(transaction));
    TransactionRsp transactionRsp = new TransactionRsp(id1, id2, 1L, amount,
            TransactionStatus.TRANS_PENDING, description, category, lastUpdateTime);
    Mockito.when(transactionService.generateListResponseFromTransactions(any()))
            .thenReturn(List.of(transactionRsp));
    RequestRsp expectedRsp = new RequestRsp();
    expectedRsp.setSuccess(true);
    expectedRsp.setCurrBalance(user1Balance);
    expectedRsp.setRequests(List.of(transactionRsp));
    expectedRsp.setMessage(message);

    // Act
    RequestRsp returnedRsp = requestService.getRequests(user1);

    // Assert
    assertEquals(returnedRsp, expectedRsp);
  }


  // TODO tests for Accept/Decline
  @Test
  public void existsRequestByIdSuccess() {
    // Arrange
    Mockito.when(transactionService.existsTransactionById(transactionId)).thenReturn(Boolean.TRUE);
    Boolean expected = Boolean.TRUE;
    // Act
    Boolean returned = requestService.existsRequestById(transactionId);
    // Assert
    assertEquals(expected, returned);
  }

  @Test
  public void getRequestByIdSuccess() {
    // Arrange
    Mockito.when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);
    // Act
    Transaction returned = requestService.getRequestById(transactionId);
    // Assert
    assertEquals(transaction, returned);
  }

  @Test
  public void saveRequestSuccess() {
    // Arrange
    Mockito.when(transactionService
            .saveTransaction(Mockito.any(Transaction.class)))
            .thenReturn(transaction);
    // Act
    Transaction returned = requestService.saveRequest(transaction);
    // Assert
    assertEquals(transaction, returned);
  }

  @Test
  public void canAcceptDeclineRequestSuccess() {
    // Arrange
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    // Act
    Boolean returned = requestService.canAcceptDeclineRequest(transactionId, id1, id2);
    // Assert
    assertEquals(Boolean.TRUE, returned);
  }

  @Test
  public void cannotAcceptDeclineRequestByWrongUids() {
    // Arrange
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    // Act && Assert
    assertEquals(Boolean.FALSE, requestService.canAcceptDeclineRequest(transactionId, id2, id1));
    assertEquals(Boolean.FALSE, requestService.canAcceptDeclineRequest(transactionId, id2, id2));
    assertEquals(Boolean.FALSE, requestService.canAcceptDeclineRequest(transactionId, id1, id1));
  }

  @Test
  public void acceptRequestSuccess() {
    // Arrange
    Mockito.when(transactionService.executeTransaction(transaction)).thenReturn(Boolean.TRUE);
    // Act
    Boolean returned = requestService.acceptRequest(transaction);
    // Assert
    assertEquals(Boolean.TRUE, returned);
  }

  @Test
  public void acceptRequestWrapperSuccess() {
    // wrapper means it's top level
    // Arrange
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    Mockito.when(requestService.acceptRequest(transaction)).thenReturn(Boolean.TRUE);
    ResourceCreatedRsp expected = new ResourceCreatedRsp(transactionId);
    // Act
    ResourceCreatedRsp returned = requestService.acceptRequest(transactionId, id1, id2);
    // Assert
    assertEquals(expected, returned);
  }

  @Test
  public void acceptRequestWrapperFailedByCannotAccept() {
    // Arrange, should fail since transaction ID does not match
    transaction.setStatus(TransactionStatus.TRANS_COMPLETE);
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
            () -> requestService.acceptRequest(transactionId, id1, id2));
  }

  @Test
  public void acceptRequestWrapperFailedByExecution() {
    // Arrange, should fail due to execution failed
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    Mockito.when(transactionService.executeTransaction(transaction)).thenReturn(Boolean.FALSE);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
            () -> requestService.acceptRequest(transactionId, id1, id2));
  }

  @Test
  public void declineRequestSuccess() {
    // Arrange
    Mockito.when(transactionService.saveTransaction(transaction)).thenReturn(transaction);
    // Act
    Boolean returned = requestService.declineRequest(transaction);
    // Assert
    assertEquals(Boolean.TRUE, returned);
    assertEquals(TransactionStatus.TRANS_DENIED, transaction.getStatus());
  }

  @Test
  public void declineRequestWrapperSuccess() {
    // wrapper means it's top level
    // Arrange
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    Mockito.when(transactionService.saveTransaction(transaction)).thenReturn(transaction);
    ResourceCreatedRsp expected = new ResourceCreatedRsp(transactionId);
    // Act
    ResourceCreatedRsp returned = requestService.declineRequest(transactionId, id1, id2);
    // Assert
    assertEquals(expected, returned);
    assertEquals(TransactionStatus.TRANS_DENIED, transaction.getStatus());
  }

  @Test
  public void declineRequestWrapperFailedByCannotAccept() {
    // Arrange, should fail since transaction ID does not match
    transaction.setStatus(TransactionStatus.TRANS_COMPLETE);
    Mockito.when(requestService.getRequestById(transactionId)).thenReturn(transaction);
    // Act & Assert
    assertThrows(InvalidUpdateException.class,
            () -> requestService.declineRequest(transactionId, id1, id2));
  }

}