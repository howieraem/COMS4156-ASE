package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import com.lgtm.easymoney.repositories.TransactionRepository;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
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
  }

  @Test
  public void transactionExistsSuccess() {
    Mockito.when(transactionRepo.existsById(transactionId)).thenReturn(Boolean.TRUE);
    boolean returned = transactionService.transactionExists(transaction);
    assertEquals(returned, Boolean.TRUE);
  }

  @Test
  public void existsTransactionByIdSuccess() {
    // todo why we have these two?
    Mockito.when(transactionRepo.existsById(transactionId)).thenReturn(Boolean.TRUE);
    boolean returned = transactionService.existsTransactionById(transactionId);
    assertEquals(returned, Boolean.TRUE);
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
    assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(transactionId));
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
            user1, user1, List.of(TransactionStatus.TRANS_PENDING))).thenReturn(List.of(transaction));
    List<Transaction> returned = transactionService.
            getAllTransactionsWithUser(user1, List.of(TransactionStatus.TRANS_PENDING));
    assertEquals(returned, List.of(transaction));
  }

  // TODO remaining tests






}