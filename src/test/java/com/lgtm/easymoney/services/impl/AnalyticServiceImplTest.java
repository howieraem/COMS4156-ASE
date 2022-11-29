package com.lgtm.easymoney.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.LoanService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;


/** Test analytic service. */
@RunWith(SpringRunner.class)
public class AnalyticServiceImplTest {
  @InjectMocks
  private AnalyticServiceImpl analyticService;
  @Mock
  private UserService userService;
  @Mock
  private FriendService friendService;
  @Mock
  private TransactionService transactionService;
  @Mock
  private LoanService loanService;

  private final User person1 = UserTestConfig.PERSON1;
  private final User person2 = UserTestConfig.PERSON2;
  private final User fin = UserTestConfig.FIN_USR;

  private Transaction transaction1;
  private Transaction transaction2;
  private Transaction transaction3;

  /** Establish transactions to show in feed, and mock behaviors of relevant services. */
  @Before
  public void setUp() {
    final Instant t = Instant.now();

    transaction1 = new Transaction();
    transaction1.setFrom(person1);
    transaction1.setTo(person2);
    transaction1.setAmount(new BigDecimal(20));
    transaction1.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction1.setLastUpdateTime(Date.from(t));
    transaction1.setCategory(Category.PARTY);

    transaction2 = new Transaction();
    transaction2.setFrom(fin);
    transaction2.setTo(person2);
    transaction2.setAmount(new BigDecimal(30));
    transaction2.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction2.setLastUpdateTime(Date.from(t.plusSeconds(1)));
    transaction2.setCategory(Category.SHOPPING);
    TransactionRsp transactionRsp = new TransactionRsp(
        transaction2.getFrom().getId(),
        transaction2.getTo().getId(),
        transaction2.getId(),
        transaction2.getAmount(),
        transaction2.getStatus(),
        transaction2.getDescription(),
        transaction2.getCategory(),
        transaction2.getLastUpdateTime());
    LoanRsp loanRsp = new LoanRsp();
    loanRsp.setLoans(List.of(transactionRsp));

    transaction3 = new Transaction();
    transaction3.setFrom(person2);
    transaction3.setTo(person1);
    transaction3.setAmount(new BigDecimal(20));
    transaction3.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction3.setLastUpdateTime(Date.from(t.plusSeconds(60)));
    transaction3.setCategory(Category.FOOD);

    Mockito.when(transactionService.getAllTransactionsWithUser(
                    person1, List.of(TransactionStatus.TRANS_COMPLETE)))
            .thenReturn(List.of(transaction1, transaction3));
    Mockito.when(transactionService.getAllTransactionsWithUser(
                    person2, List.of(TransactionStatus.TRANS_COMPLETE)))
            .thenReturn(List.of(transaction1, transaction2, transaction3));
    Mockito.when(transactionService.getAllTransactionsWithUser(
                    fin, List.of(TransactionStatus.TRANS_COMPLETE)))
            .thenReturn(List.of(transaction2));
    Mockito.doReturn(loanRsp).when(loanService).getLoansByUser(fin);

    Mockito.when(userService.getUserById(person1.getId())).thenReturn(person1);
    Mockito.when(userService.getUserById(person2.getId())).thenReturn(person2);
    Mockito.when(userService.getUserById(fin.getId())).thenReturn(fin);
  }

  // Note: incl. extra assertions to cover branches of FeedActivityRsp.equals()
  @Test
  public void testGetAnalytic() {
    var analyticRsp = analyticService.getAnalytic(person1);
    assertNotNull(analyticRsp);
    assertEquals(1, analyticRsp.getReport().size());
    assertEquals(BigDecimal.valueOf(20), analyticRsp.getExpenditure());

    var analyticRsp1 = analyticService.getAnalytic(fin);
    assertEquals(1, analyticRsp1.getFinance().size());
    assertEquals(TransactionStatus.TRANS_COMPLETE,
        analyticRsp1.getFinance().get(person2.getEmail()));

    var analyticRsp2 = analyticService.getAnalytic(person2);
    assertNotNull(analyticRsp2);
    assertEquals(1, analyticRsp2.getReport().size());
    assertEquals(BigDecimal.valueOf(20), analyticRsp2.getExpenditure());
  }
}
