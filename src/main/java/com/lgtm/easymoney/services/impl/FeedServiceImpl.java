package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.FeedActivityRsp;
import com.lgtm.easymoney.payload.rsp.FeedRsp;
import com.lgtm.easymoney.services.FeedService;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * service for Feed.
 */
@Service
public class FeedServiceImpl implements FeedService {
  private final UserService userService;
  private final FriendService friendService;
  private final TransactionService transactionService;
  private static final int FEED_SIZE = 20;

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
  private List<FeedActivityRsp> getFeedByUser(User u) {
    // get user's activity
    List<FeedActivityRsp> res = getUserActivity(u, false);
    // get friends' activity,hide amount
    List<User> friends = friendService.getFriends(u);
    for (User f : friends) {
      List<FeedActivityRsp> fs = getUserActivity(f, true);
      res.addAll(fs);
    }
    // remove duplicates by checking fromUid, toUid and time
    res = res.stream().distinct().collect(Collectors.toList());
    // sort, the latest first
    res.sort(Comparator.comparing(FeedActivityRsp::getLastUpdateTime));
    // only return 20 latest and valid transaction
    return res.stream().limit(FEED_SIZE).toList();
  }

  /**
   * internal, return a specific user's own activities and map to payload response.
   *
   * @param u user
   * @param hideAmount should hide amount or not, privacy
   * @return (mutable) list of user's own feed activity response
   */
  private List<FeedActivityRsp> getUserActivity(User u, boolean hideAmount) {
    // get transactions
    List<Transaction> userActivity = transactionService.getAllTransactionsWithUser(u,
            List.of(TransactionStatus.TRANS_COMPLETE));
    // mapping
    return userActivity.stream()
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
            .collect(Collectors.toCollection(ArrayList::new));

  }

  /**
   * INTERNAL helper method to set promotion text.
   *
   * @param activities list of users' activity
   */
  private void setPromoText(List<FeedActivityRsp> activities) {
    // set promo text
    for (FeedActivityRsp activity : activities) {
      User from = userService.getUserById(activity.getFromUid());
      User to = userService.getUserById(activity.getToUid());
      // get the one that has promo: EXCEPT personal
      User promo = from.getType() != UserType.PERSONAL ? from :
          (to.getType() != UserType.PERSONAL ? to : null);
      if (promo != null) {
        activity.setPromoText(promo.getBizPromotionText());
      }
    }
  }

  /**
   * EXTERNAL get user's feed with uid.
   *
   * @param user current user
   * @return response containing list of user's feed activity
   */
  @Override
  public FeedRsp getFeed(User user) {
    // get user's feed
    List<FeedActivityRsp> activities = getFeedByUser(user);
    // set promo text
    setPromoText(activities);

    return new FeedRsp(activities);
  }
}
