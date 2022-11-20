package com.lgtm.easymoney.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.services.FriendService;
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


/** Test friend feed service. */
@RunWith(SpringRunner.class)
public class FeedServiceImplTest {
  @InjectMocks
  private FeedServiceImpl feedService;

  @Mock
  private UserService userService;

  @Mock
  private FriendService friendService;

  @Mock
  private TransactionService transactionService;

  private final User person1 = UserTestConfig.PERSON1;
  private final User person2 = UserTestConfig.PERSON2;
  private final User biz = UserTestConfig.BIZ_USR;
  private final User fin = UserTestConfig.FIN_USR;

  private Transaction transaction1;
  private Transaction transaction2;
  private Transaction transaction3;
  private Transaction transaction4;

  /** Establish transactions to show in feed, and mock behaviors of relevant services. */
  @Before
  public void setUp() {
    final Instant t = Instant.now();

    transaction1 = new Transaction();
    transaction1.setFrom(person1);
    transaction1.setTo(biz);
    transaction1.setAmount(new BigDecimal(1));
    transaction1.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction1.setLastUpdateTime(Date.from(t));

    transaction2 = new Transaction();
    transaction2.setFrom(person1);
    transaction2.setTo(person2);
    transaction2.setAmount(new BigDecimal(2));
    transaction2.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction2.setLastUpdateTime(Date.from(t));

    transaction3 = new Transaction();
    transaction3.setFrom(fin);
    transaction3.setTo(person2);
    transaction3.setAmount(new BigDecimal(3));
    transaction3.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction3.setLastUpdateTime(Date.from(t.plusSeconds(1)));

    transaction4 = new Transaction();
    transaction4.setFrom(person1);
    transaction4.setTo(person2);
    transaction4.setAmount(new BigDecimal(2));
    transaction4.setStatus(TransactionStatus.TRANS_COMPLETE);
    transaction4.setLastUpdateTime(Date.from(t.plusSeconds(60)));

    Mockito.when(transactionService.getAllTransactionsWithUser(
            person1, List.of(TransactionStatus.TRANS_COMPLETE)))
        .thenReturn(List.of(transaction1, transaction2, transaction4));
    Mockito.when(transactionService.getAllTransactionsWithUser(
            person2, List.of(TransactionStatus.TRANS_COMPLETE)))
        .thenReturn(List.of(transaction2, transaction3, transaction4));

    Mockito.when(userService.getUserById(person1.getId())).thenReturn(person1);
    Mockito.when(userService.getUserById(person2.getId())).thenReturn(person2);
    Mockito.when(userService.getUserById(biz.getId())).thenReturn(biz);
    Mockito.when(userService.getUserById(fin.getId())).thenReturn(fin);

    Mockito.when(friendService.getFriends(person1)).thenReturn(List.of(person2));
    Mockito.when(friendService.getFriends(person2)).thenReturn(List.of(person1));
  }

  // Note: incl. extra assertions to cover branches of FeedActivityRsp.equals()
  @Test
  public void testGetFeed() {
    var feedRsp = feedService.getFeed(person1);

    assertNotNull(feedRsp);
    var activities = feedRsp.getActivities();
    assertEquals(4, activities.size());

    var a1 = activities.get(0);
    assertNotNull(a1);
    assertEquals(a1.getLastUpdateTime(), transaction1.getLastUpdateTime());
    assertNotNull(a1.getAmount());
    assertEquals(a1.getPromoText(), biz.getBizPromotionText());

    var a2 = activities.get(1);
    assertNotNull(a2);
    assertNotEquals(a1, a2);
    assertNotNull(a2.getAmount());
    assertEquals(a2.getLastUpdateTime(), transaction2.getLastUpdateTime());
    assertNull(a2.getPromoText());

    var a3 = activities.get(2);
    assertNotNull(a3);
    assertNotEquals(a2, a3);
    assertNotEquals(a1, a3);
    assertEquals(a3.getLastUpdateTime(), transaction3.getLastUpdateTime());
    assertNull(a3.getAmount());
    assertEquals(a3.getPromoText(), fin.getBizPromotionText());

    var a4 = activities.get(3);
    assertNotNull(a4);
    assertNotEquals(a2, a4);
    assertNotNull(a4.getAmount());
    assertEquals(a4.getLastUpdateTime(), transaction4.getLastUpdateTime());
    assertNull(a4.getPromoText());
  }
}
