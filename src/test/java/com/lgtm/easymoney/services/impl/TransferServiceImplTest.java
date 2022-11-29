package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;
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
 * Unit test for TransferService.
 * {@link com.lgtm.easymoney.services.impl.TransferServiceImpl}
 * */
@RunWith(SpringRunner.class)
public class TransferServiceImplTest {
  @Mock
  private TransactionService transactionService;
  @Mock
  private UserService userService;
  @InjectMocks
  private TransferServiceImpl transferService;
  private TransferReq transferReq;
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
  private String message = "User's transfers returned";

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // transferReq
    transferReq = new TransferReq();
    transferReq.setToUid(id2);
    transferReq.setAmount(amount);
    transferReq.setCategory(String.valueOf(category));
    transferReq.setDescription(description);
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

  @Test
  public void makeTransferSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    Mockito.when(userService.getUserById(id2)).thenReturn(user2);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    Mockito.when(transactionService.executeTransaction(transaction)).thenReturn(true);
    ResourceCreatedRsp expectedRsp = new ResourceCreatedRsp(transactionId);

    // Act
    ResourceCreatedRsp returnedRsp = transferService.makeTransfer(user1, transferReq);

    // Assert
    assertEquals(returnedRsp, expectedRsp);
  }

  @Test
  public void makeTransferFailedWithFailedExecuteTransaction() {
    // Arrange
    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    Mockito.when(userService.getUserById(id2)).thenReturn(user2);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);
    Mockito.when(transactionService.executeTransaction(transaction)).thenReturn(false);
    transaction.setStatus(TransactionStatus.TRANS_FAILED);
    Mockito.when(transactionService.saveTransaction(any())).thenReturn(transaction);

    // Act & Assert
    assertThrows(InvalidUpdateException.class,
        () -> transferService.makeTransfer(user1, transferReq));
    assertEquals(TransactionStatus.TRANS_FAILED, transaction.getStatus());
  }

  @Test
  public void getTransfersByUidSuccess() {
    // Arrange
    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    transaction.setStatus(TransactionStatus.TRANS_COMPLETE);
    Mockito.when(transactionService.getAllTransactionsWithUser(any(), any()))
        .thenReturn(List.of(transaction));
    TransactionRsp transactionRsp = new TransactionRsp(id1, id2, 1L, amount,
        TransactionStatus.TRANS_COMPLETE, description, category, lastUpdateTime);
    Mockito.when(transactionService.generateListResponseFromTransactions(any()))
        .thenReturn(List.of(transactionRsp));
    TransferRsp expectedRsp = new TransferRsp();
    expectedRsp.setSuccess(true);
    expectedRsp.setCurrBalance(user1Balance);
    expectedRsp.setTransfers(List.of(transactionRsp));
    expectedRsp.setMessage(message);

    // Act
    TransferRsp returnedRsp = transferService.getTransfers(user1);

    // Assert
    assertEquals(returnedRsp, expectedRsp);
  }
}