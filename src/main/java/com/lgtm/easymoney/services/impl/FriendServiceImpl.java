package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.ProfilesRsp;
import com.lgtm.easymoney.repositories.FriendshipRepository;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO after we have auth, move the inapplicable op checks to a HandlerInterceptor instead
@Service
public class FriendServiceImpl implements FriendService {
  private final UserService userService;

  @Autowired
  private final FriendshipRepository friendshipRepository;

  @Autowired
  public FriendServiceImpl(UserService userService, FriendshipRepository friendshipRepository) {
    this.userService = userService;
    this.friendshipRepository = friendshipRepository;
  }

  @Override
  public Friendship getFriendshipRecord(User u1, User u2) {
    return friendshipRepository.findByUser1AndUser2(u1, u2);
  }

  @Override
  public void addFriend(FriendshipReq friendshipReq) {
    User requester = userService.getUserById(friendshipReq.getUid1());
    if (requester.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid1(), "uid1", "addFriend");
    }
    User acceptor = userService.getUserById(friendshipReq.getUid2());
    if (acceptor.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid2(), "uid2", "addFriend");
    }

    var fs1 = new Friendship();
    fs1.setUser1(requester);
    fs1.setUser2(acceptor);
    friendshipRepository.save(fs1);
    // The other friendship direction can NOT be saved at this stage
    // because we don't want the requester to accept.
  }

  @Override
  public void acceptFriend(FriendshipReq friendshipReq) {
    User acceptor = userService.getUserById(friendshipReq.getUid1());
    if (acceptor.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid1(), "uid1", "acceptFriend");
    }
    User requester = userService.getUserById(friendshipReq.getUid2());
    if (requester.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid2(), "uid2", "acceptFriend");
    }

    var fs1 = getFriendshipRecord(requester, acceptor);
    if (fs1 == null) {
      throw new ResourceNotFoundException("Friendship", "uid2", friendshipReq.getUid2());
    }
    fs1.setActive(Boolean.TRUE);
    var fs2 = new Friendship();
    fs2.setUser1(acceptor);
    fs2.setUser2(requester);
    fs2.setActive(Boolean.TRUE);
    friendshipRepository.save(fs1);
    friendshipRepository.save(fs2);
  }

  @Override
  public void delFriend(FriendshipReq friendshipReq) {
    User u1 = userService.getUserById(friendshipReq.getUid1());
    if (u1.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid1(), "uid1", "delFriend");
    }
    User u2 = userService.getUserById(friendshipReq.getUid2());
    if (u2.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", friendshipReq.getUid2(), "uid2", "delFriend");
    }

    var fs1 = getFriendshipRecord(u1, u2);
    if (fs1 == null) {
      throw new ResourceNotFoundException("Friendship", "uid2", friendshipReq.getUid2());
    }
    friendshipRepository.deleteByUser1AndUser2(u1, u2);
    if (getFriendshipRecord(u2, u1) != null) {
      friendshipRepository.deleteByUser1AndUser2(u2, u1);
    }
  }

  @Override
  public List<User> getFriends(User u) {
    if (u.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", u.getId(), "uid", "getFriends");
    }

    List<User> res = new ArrayList<>();
    for (var fs : u.getFriendships()) {
      if (fs.getActive()) {
        res.add(fs.getUser2());
      }
    }
    return res;
  }

  @Override
  public ProfilesRsp getFriends(Long uid) {
    var u = userService.getUserById(uid);
    if (u.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", u.getId(), "uid", "getFriends");
    }

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : u.getFriendships()) {
      if (fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser2()));
      }
    }
    return new ProfilesRsp(Boolean.TRUE, res);
  }

  @Override
  public List<User> getFriendsPending(User u) {
    if (u.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", u.getId(), "uid", "getFriendsPending");
    }

    List<User> res = new ArrayList<>();
    for (var fs : friendshipRepository.findByUser2(u)) {
      if (!fs.getActive()) {
        res.add(fs.getUser1());
      }
    }
    return res;
  }

  @Override
  public ProfilesRsp getFriendsPending(Long uid) {
    var u = userService.getUserById(uid);
    if (u.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "User", u.getId(), "uid", "getFriendsPending");
    }

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : friendshipRepository.findByUser2(u)) {
      if (!fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser1()));
      }
    }
    return new ProfilesRsp(Boolean.TRUE, res);
  }
}
