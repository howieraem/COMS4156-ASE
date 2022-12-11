package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.repositories.TransactionRepository;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for TransactionService.
 * {@link com.lgtm.easymoney.services.impl.TransactionServiceImpl}
 * */
@RunWith(SpringRunner.class)
public class TransactionServiceImplTest {
  @InjectMocks
  private TransactionServiceImpl transactionService;
  @Mock
  private UserService userService;
  @Mock
  private TransactionRepository transactionRepo;
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
    transaction.setLastUpdateTime(lastUpdateTime);
  }

  @Test
  public void transactionExistsSuccess() {
    Mockito.when(transactionRepo.existsById(transactionId)).thenReturn(Boolean.TRUE);
    boolean returned = transactionService.transactionExists(transaction);
    assertEquals(Boolean.TRUE, returned);
  }

  @Test
  public void existsTransactionByIdSuccess() {
    // todo why we have these two?
    Mockito.when(transactionRepo.existsById(transactionId)).thenReturn(Boolean.TRUE);
    boolean returned = transactionService.existsTransactionById(transactionId);
    assertEquals(Boolean.TRUE, returned);
  }

  @Test
  public void getTransactionByIdSuccess() {
    // todo why we have these two?
    Mockito.when(transactionRepo.findById(transactionId)).thenReturn(Optional.of(transaction));
    Transaction returned = transactionService.getTransactionById(transactionId);
    assertEquals(returned, transaction);
  }

  @Test
  public void getTransactionByIdFailedByNotFound() {
    Mockito.when(transactionRepo.findById(transactionId)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> transactionService.getTransactionById(transactionId));
  }

  @Test
  public void saveTransactionSuccess() {
    Mockito.when(transactionRepo.save(transaction)).thenReturn(transaction);
    Transaction returned = transactionService.saveTransaction(transaction);
    assertEquals(returned, transaction);
  }

  @Test
  public void getAllTransactionsSuccess() {
    Mockito.when(transactionRepo.findAll()).thenReturn(List.of(transaction));
    List<Transaction> returned = transactionService.getAllTransactions();
    assertEquals(returned, List.of(transaction));
  }

  @Test
  public void getAllTransactionsWithUserSuccess() {
    // todo maybe more data for testing? to ensure the list returns more
    Mockito.when(transactionRepo.findByFromOrToAndStatusIn(
            user1, user1, List.of(TransactionStatus.TRANS_PENDING)))
        .thenReturn(List.of(transaction));
    List<Transaction> returned =
        transactionService.getAllTransactionsWithUser(
            user1,
            List.of(TransactionStatus.TRANS_PENDING));
    assertEquals(returned, List.of(transaction));
  }

  @Test
  public void executeTransactionSuccess() {
    // Arrange
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    Mockito.when(userService.saveUser(user1)).thenReturn(user1);
    Mockito.when(userService.saveUser(user2)).thenReturn(user2);
    Mockito.when(transactionRepo.save(transaction)).thenReturn(transaction);
    BigDecimal expectedSenderBalance = user1.getBalance().subtract(transaction.getAmount());
    BigDecimal expectedReceiverBalance = user2.getBalance().add(transaction.getAmount());
    TransactionStatus expectedStatus = TransactionStatus.TRANS_COMPLETE;

    // Act
    final boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.TRUE, returned);
  }

  @Test
  public void executeTransactionFailedByTransactionNotExists() {
    // Arrange
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(false);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    BigDecimal expectedSenderBalance = user1.getBalance();
    BigDecimal expectedReceiverBalance = user2.getBalance();

    // Act
    boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void executeTransactionFailedByFromUserNotExists() {
    // Arrange
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(false);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    BigDecimal expectedReceiverBalance = user2.getBalance();
    TransactionStatus expectedStatus = TransactionStatus.TRANS_PENDING;

    // Act
    boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void executeTransactionFailedByToUserNotExists() {
    // Arrange
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(false);
    BigDecimal expectedSenderBalance = user1.getBalance();
    TransactionStatus expectedStatus = TransactionStatus.TRANS_PENDING;

    // Act
    boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void executeTransactionFailedByUnsupportedStatus() {
    // Arrange
    transaction.setStatus(TransactionStatus.TRANS_COMPLETE);
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    BigDecimal expectedSenderBalance = user1.getBalance();
    BigDecimal expectedReceiverBalance = user2.getBalance();
    TransactionStatus expectedStatus = transaction.getStatus();

    // Act
    final boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void executeTransactionFailedByIdenticalSenderReceiver() {
    // Arrange
    transaction.setTo(user1);
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    BigDecimal expectedSenderBalance = user1.getBalance();
    BigDecimal expectedReceiverBalance = user2.getBalance();
    TransactionStatus expectedStatus = transaction.getStatus();

    // Act
    final boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void executeTransactionFailedByInsufficientBalance() {
    // Arrange
    transaction.setAmount(new BigDecimal(100));
    Mockito.when(transactionRepo.existsById(transaction.getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getFrom().getId())).thenReturn(true);
    Mockito.when(userService.existsById(transaction.getTo().getId())).thenReturn(true);
    BigDecimal expectedSenderBalance = user1.getBalance();
    BigDecimal expectedReceiverBalance = user2.getBalance();
    TransactionStatus expectedStatus = transaction.getStatus();

    // Act
    final boolean returned = transactionService.executeTransaction(transaction);

    // Assert
    assertEquals(user1.getBalance(), expectedSenderBalance);
    assertEquals(user2.getBalance(), expectedReceiverBalance);
    assertEquals(transaction.getStatus(), expectedStatus);
    assertEquals(Boolean.FALSE, returned);
  }

  @Test
  public void generateResponseFromTransactionSuccess() {
    TransactionRsp expectedRsp = new TransactionRsp(id1, id2, transaction.getId(), amount,
        TransactionStatus.TRANS_PENDING, description, category, lastUpdateTime);
    TransactionRsp returnedRsp = transactionService.generateResponseFromTransaction(transaction);
    assertEquals(returnedRsp, expectedRsp);
  }

  @Test
  public void generateListResponseFromTransactionsSuccess() {
    List<TransactionRsp> expectedRsp = List.of(
        new TransactionRsp(id1, id2, transaction.getId(), amount,
                           TransactionStatus.TRANS_PENDING, description, category, lastUpdateTime));
    List<TransactionRsp> returnedRsp =
        transactionService.generateListResponseFromTransactions(List.of(transaction));
    assertEquals(returnedRsp, expectedRsp);
  }
}