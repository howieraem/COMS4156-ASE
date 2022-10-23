package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.FeedActivityRsp;
import com.lgtm.easymoney.payload.FeedRsp;
import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.ProfilesRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.repositories.FriendshipRepository;
import com.lgtm.easymoney.services.FeedService;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.saxon.trans.SymbolicName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * service for Feed.
 */
@Service
@Transactional(rollbackFor = Exception.class)  // required for delete
public class FeedServiceImpl implements FeedService {
  private final UserService userService;
  private final FriendService friendService;
  private final TransactionService transactionService;

  /**
   * feed service for getting user's feed activity.
   *
   * @param userService userservice
   * @param friendService friend service
   * @param transactionService trans service
   */
  @Autowired
  public FeedServiceImpl(UserService userService, FriendService friendService,
                         TransactionService transactionService) {
    this.userService = userService;
    this.friendService = friendService;
    this.transactionService = transactionService;
  }

  @Override
  public List<Transaction> getFeedByUser(User u) {
    // only return 20 latest and valid transaction
    List<Transaction> res = new ArrayList<>();
    // get user's activity
    List<Transaction> userActivity = transactionService.getAllTransactionsWithUser(u,
            List.of(TransactionStatus.TRANS_COMPLETE));
    res.addAll(userActivity);
    // get friends' activity
    List<User> friends = friendService.getFriends(u);
    for (User f : friends) {
      List<Transaction> fs = transactionService
              .getAllTransactionsWithUser(u, List.of(TransactionStatus.TRANS_COMPLETE));
      res.addAll(fs);
    }
    // remove duplicates, 1->2 and 2->1 are same transaction, only need 1 in user1's feed
    res = res.stream().distinct().collect(Collectors.toList());
    // sort, the latest first
    Collections.sort(res, Comparator.comparing(Transaction::getLastUpdateTime));

    return res;
  }

  @Override
  public FeedRsp getFeedByUid(Long uid) {
    List<Transaction> activity = getFeedByUser(userService.getUserById(uid));
    // map
    List<FeedActivityRsp> activities =
            activity.stream()
                    .map(a -> new FeedActivityRsp(
                            a.getFrom().getId(),
                            a.getTo().getId(),
                            a.getAmount().toString(),
                            a.getDescription(),
                            a.getLastUpdateTime(),
                            null  // placeholder
                            ))
                    .collect(Collectors.toList());
    //  todo hide friend's amounts in activities? we value privacy!

    // set promo text
    for (FeedActivityRsp a : activities) {
      User from = userService.getUserById(a.getFromUid());
      User to = userService.getUserById(a.getToUid());
      // get the one that's business
      User biz = from.getType() == UserType.BUSINESS ? from :
              (to.getType() == UserType.BUSINESS ? to : null);
      if (biz != null) {
        a.setPromoText(biz.getBizProfile().getPromotionText());
      }
    }

    return new FeedRsp(activities);

  }


}
