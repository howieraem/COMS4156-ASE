package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.FriendshipReq;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import java.util.List;

/** Interface of service for friendship. */
public interface FriendService {
  Friendship getFriendshipRecord(User u1, User u2);

  void checkUserType(User user);

  void addFriend(User requester, FriendshipReq friendshipReq);

  void acceptFriend(User acceptor, FriendshipReq friendshipReq);

  void delFriend(User current, Long friendUid);

  List<User> getFriends(User u);  // for internal

  ProfilesRsp getFriendProfiles(User current);  // for external

  // to be accepted by uid, not requested by uid
  ProfilesRsp getFriendProfilesPending(User current);
}
