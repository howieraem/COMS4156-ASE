package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.FriendshipReq;
import com.lgtm.easymoney.payload.rsp.ProfileRsp;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import com.lgtm.easymoney.repositories.FriendshipRepository;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * service for friendship.
 */
@Service
@Transactional(rollbackFor = Exception.class)  // required for delete
public class FriendServiceImpl implements FriendService {
  private final UserService userService;

  private final FriendshipRepository friendshipRepository;

  private static final String RESOURCE = "Friendship";

  @Autowired
  public FriendServiceImpl(UserService userService, FriendshipRepository friendshipRepository) {
    this.userService = userService;
    this.friendshipRepository = friendshipRepository;
  }

  /**
   * Check to see if the user is a personal user;
   * all other types of user are inapplicable for friend service.
   */
  public void checkUserType(User user) {
    if (user.getType() != UserType.PERSONAL) {
      throw new InapplicableOperationException(
              "User", user.getId(), "uid", "friend");
    }
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
  public void addFriend(User requester, FriendshipReq friendshipReq) {
    checkUserType(requester);
    User acceptor = userService.getUserById(friendshipReq.getUid());
    checkUserType(acceptor);

    if (getFriendshipRecord(requester, acceptor) != null) {
      // If this case is not covered here, JPA will throw EntityExistsException instead
      // which is harder to extract the table and the field info
      throw new DataIntegrityViolationException(
          "Friendship already requested/accepted!",
          new ConstraintViolationException("", null, "friendship.PRIMARY"));
    }

    var fs1 = new Friendship();
    fs1.setUser1(requester);
    fs1.setUser2(acceptor);
    fs1.setNote(friendshipReq.getNote());
    friendshipRepository.save(fs1);
    // The other friendship direction can NOT be saved at this stage
    // because we don't want the requester to accept.
  }

  /**
   * Accepting an existing friendship record;
   * set it as an active friendship.
   */
  @Override
  public void acceptFriend(User acceptor, FriendshipReq friendshipReq) {
    checkUserType(acceptor);
    User requester = userService.getUserById(friendshipReq.getUid());
    // No need to checkUserType(requester) because already checked in prerequisite addFriend()

    var fs1 = getFriendshipRecord(requester, acceptor);
    if (fs1 == null) {
      throw new ResourceNotFoundException(RESOURCE, "uid", friendshipReq.getUid());
    }
    if (fs1.getActive()) {
      throw new InvalidUpdateException(
          RESOURCE, fs1.getKeyString(), "uid", friendshipReq.getUid());
    }

    fs1.setActive(Boolean.TRUE);
    var fs2 = new Friendship();
    fs2.setUser1(acceptor);
    fs2.setUser2(requester);
    fs2.setNote(friendshipReq.getNote());
    fs2.setActive(Boolean.TRUE);
    // fs1 and fs2 may not have the same note
    friendshipRepository.save(fs1);
    friendshipRepository.save(fs2);
  }

  /**
   * Delete ab existing friendship.
   */
  @Override
  public void delFriend(User current, Long friendUid) {
    checkUserType(current);
    User friend = userService.getUserById(friendUid);
    // we don't need to check friend user type because existing friendships are of PERSONAL anyway

    var fs1 = getFriendshipRecord(current, friend);
    if (fs1 == null) {
      throw new ResourceNotFoundException(RESOURCE, "uid", friendUid);
    }
    friendshipRepository.delete(fs1);

    // Note: can delete friendship not yet accepted
    var fs2 = getFriendshipRecord(friend, current);
    if (fs2 != null) {
      friendshipRepository.delete(fs2);
    }
  }

  /**
   * Get all friends of a user.
   *
   * @return A list of users that are friends with the user in the parameter
   */
  @Override
  public List<User> getFriends(User u) {
    checkUserType(u);

    List<User> res = new ArrayList<>();
    for (var fs : u.getFriendships()) {
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
  public ProfilesRsp getFriendProfiles(User current) {
    checkUserType(current);

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : current.getFriendships()) {
      if (fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser2()));
      }
    }
    return new ProfilesRsp(res);
  }

  /**
   * Get all pending friends by id. Returns a list of user profiles that
   * are still pending friends with the user given.
   */
  @Override
  public ProfilesRsp getFriendProfilesPending(User current) {
    checkUserType(current);

    List<ProfileRsp> res = new ArrayList<>();
    for (var fs : friendshipRepository.findByUser2(current)) {
      if (!fs.getActive()) {
        res.add(new ProfileRsp(fs.getUser1()));
      }
    }
    return new ProfilesRsp(res);
  }
}
