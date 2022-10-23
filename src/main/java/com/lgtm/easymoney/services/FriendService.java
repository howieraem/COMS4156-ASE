package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfilesRsp;
import java.util.List;

/** Interface of service for friendship. */
public interface FriendService {
  Friendship getFriendshipRecord(User u1, User u2);

  void addFriend(FriendshipReq friendshipReq);

  void acceptFriend(FriendshipReq friendshipReq);

  void delFriend(FriendshipReq friendshipReq);

  List<User> getFriends(User u);  // for internal

  ProfilesRsp getFriends(Long uid);  // for external

  ProfilesRsp getFriendsPending(Long uid);  // to be accepted by uid, not requested by uid
}
