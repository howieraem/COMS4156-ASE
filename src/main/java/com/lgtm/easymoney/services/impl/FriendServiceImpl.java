package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
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
import org.springframework.transaction.annotation.Transactional;


/**
 * service for friendship.
 * TODO after we have auth, move the inapplicable op checks to a HandlerInterceptor instead
 */
@Service
@Transactional(rollbackFor = Exception.class)  // required for delete
public class FriendServiceImpl implements FriendService {
  private final UserService userService;

  private final FriendshipRepository friendshipRepository;

  @Autowired
  public FriendServiceImpl(UserService userService, FriendshipRepository friendshipRepository) {
    this.userService = userService;
    this.friendshipRepository = friendshipRepository;
  }

  /**
   * Check to see if the user is a personal user;
   * all other types of user are inapplicable for friend service.
   */
  @Override
  public User checkUserType(User user) {
    if (user.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
              "User", user.getId(), "uid1", "addFriend");
    }
    return user;
  }

  /**
   * Retrieve the friendship of two users with their ids.
   */
  @Override
  public Friendship getFriendshipRecord(User u1, User u2) {
    return friendshipRepository.findByUser1AndUser2(u1, u2);
  }

  /**
   * Setting an inactive friendship between two users.
   */
  @Override
  public void addFriend(FriendshipReq friendshipReq) {
    User u1 = checkUserType(userService.getUserById(friendshipReq.getUid1()));
    User u2 = checkUserType(userService.getUserById(friendshipReq.getUid2()));

    var fs1 = new Friendship();
    fs1.setUser1(u1);
    fs1.setUser2(u2);
    friendshipRepository.save(fs1);
    // The other friendship direction can NOT be saved at this stage
    // because we don't want the requester to accept.
  }

  /**
   * Accepting an existing friendship record;
   * set it as an active friendship.
   */
  @Override
  public void acceptFriend(FriendshipReq friendshipReq) {
    User u1 = checkUserType(userService.getUserById(friendshipReq.getUid1()));
    User u2 = checkUserType(userService.getUserById(friendshipReq.getUid2()));

    var fs1 = getFriendshipRecord(u2, u1);
    if (fs1 == null) {
      throw new ResourceNotFoundException("Friendship", "uid2", friendshipReq.getUid2());
    }
    if (fs1.getActive()) {
      throw new InvalidUpdateException(
          "Friendship", fs1.getKeyString(), "uid2", friendshipReq.getUid2());
    }

    fs1.setActive(Boolean.TRUE);
    var fs2 = new Friendship();
    fs2.setUser1(u1);
    fs2.setUser2(u2);
    fs2.setActive(Boolean.TRUE);
    friendshipRepository.save(fs1);
    friendshipRepository.save(fs2);
  }

  /**
   * Delete ab existing friendship.
   */
  @Override
  public void delFriend(FriendshipReq friendshipReq) {
    User u1 = checkUserType(userService.getUserById(friendshipReq.getUid1()));
    User u2 = checkUserType(userService.getUserById(friendshipReq.getUid2()));

    var fs1 = getFriendshipRecord(u1, u2);
    if (fs1 == null) {
      throw new ResourceNotFoundException("Friendship", "uid2", friendshipReq.getUid2());
    }

    // Note: can delete friendship not yet accepted
    friendshipRepository.deleteByUser1AndUser2(u1, u2);
    if (getFriendshipRecord(u2, u1) != null) {
      friendshipRepository.deleteByUser1AndUser2(u2, u1);
    }
  }

  /**
   * Get all friends of a user.
   *
   * @return A list of users that are friends with the user in the parameter
   */
  @Override
  public List<User> getFriends(User u) {
    User user = checkUserType(u);

    List<User> res = new ArrayList<>();
    for (var fs : user.getFriendships()) {
      if (fs.getActive()) {
        res.add(fs.getUser2());
      }
    }
    return res;
  }

  /**
   * Get all friends by id.
   *
   * @return A list of user profiles that are friends with the user in the parameter
   */
  @Override
  public ProfilesRsp getFriends(Long uid) {
    var u = checkUserType(userService.getUserById(uid));

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : u.getFriendships()) {
      if (fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser2()));
      }
    }
    return new ProfilesRsp(res);
  }

  /** Get all pending friends by id.
   * Return: a list of user profiles that
   * are still pending friends with the user in the parameter
   */
  @Override
  public ProfilesRsp getFriendsPending(Long uid) {
    var u = checkUserType(userService.getUserById(uid));

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : friendshipRepository.findByUser2(u)) {
      if (!fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser1()));
      }
    }
    return new ProfilesRsp(res);
  }
}
