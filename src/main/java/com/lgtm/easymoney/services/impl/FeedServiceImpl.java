package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.FeedActivityRsp;
import com.lgtm.easymoney.payload.FeedRsp;
import com.lgtm.easymoney.services.FeedService;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
  private final int feedSize = 20;

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

  /**
   * return's user's Feed: own activity + friends' activity.
   *
   * @param u user
   * @return list of feed activity response.
   */
  @Override
  public List<FeedActivityRsp> getFeedByUser(User u) {
    List<FeedActivityRsp> res = new ArrayList<>();
    // get user's activity
    List<FeedActivityRsp> userActivity = getUserActivity(u, false);
    res.addAll(userActivity);
    // get friends' activity,hide amount
    List<User> friends = friendService.getFriends(u);
    for (User f : friends) {
      List<FeedActivityRsp> fs = getUserActivity(f, true);
      res.addAll(fs);
    }
    // remove duplicates, 1->2 and 2->1 are same transaction
    res = res.stream().distinct().collect(Collectors.toList());
    // sort, the latest first
    Collections.sort(res, Comparator.comparing(FeedActivityRsp::getLastUpdateTime));
    // only return 20 latest and valid transaction
    return res.stream().limit(feedSize).collect(Collectors.toList());
  }

  /**
   * internal, return a specific user's own activities and map to payload response.
   *
   * @param u user
   * @param hideAmount should hide amount or not, privacy
   * @return list of user's own feed activity response
   */
  private List<FeedActivityRsp> getUserActivity(User u, boolean hideAmount) {
    // get transactions
    List<Transaction> userActivity = transactionService.getAllTransactionsWithUser(u,
            List.of(TransactionStatus.TRANS_COMPLETE));
    // mapping
    List<FeedActivityRsp> activities =
            userActivity.stream()
                    .map(a -> new FeedActivityRsp(
                            a.getFrom().getId(),
                            a.getTo().getId(),
                            a.getFrom().getType(),
                            a.getTo().getType(),
                            a.getCategory(),
                            hideAmount ? null : a.getAmount(),
                            a.getDescription(),
                            a.getLastUpdateTime(),
                            null  // placeholder
                    ))
                    .collect(Collectors.toList());
    return activities;

  }

  private List<FeedActivityRsp> setPromoText(List<FeedActivityRsp> activities) {
    // set promo text
    for (FeedActivityRsp a : activities) {
      User from = userService.getUserById(a.getFromUid());
      User to = userService.getUserById(a.getToUid());
      // get the one that has promo: EXCEPT personal
      User promo = from.getType() != UserType.PERSONAL ? from :
              (to.getType() != UserType.PERSONAL ? to : null);
      if (promo != null) {
        a.setPromoText(promo.getBizProfile().getPromotionText());
      }
    }
    return activities;
  }

  @Override
  public FeedRsp getFeedByUid(Long uid) {
    // get user's feed
    List<FeedActivityRsp> activities = getFeedByUser(userService.getUserById(uid));
    // set promo text
    activities = setPromoText(activities);

    return new FeedRsp(activities);

  }


}