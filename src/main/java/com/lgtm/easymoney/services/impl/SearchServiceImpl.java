package com.lgtm.easymoney.services.impl;


import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.ProfileRsp;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import com.lgtm.easymoney.repositories.AccountRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.SearchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * search service implementation. helps find user profiles given info.
 */
@Service
public class SearchServiceImpl implements SearchService {
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public SearchServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
  }

  /**
   * Retrieve a user by id.
   * Param: id
   * Return: A user
   */
  @Override
  public User getUserById(Long id) {
    var userWrapper = userRepository.findById(id);
    if (userWrapper.isEmpty()) {
      throw new ResourceNotFoundException("User", "id", id);
    }

    return userWrapper.get();
  }

  /**
   * Retrieve users by account name.
   * Param: accountName
   * Return: A list of users whose account names match the search string
   */
  @Override
  public List<User> getUserByName(String accountName) {
    var accountWrapper = accountRepository.findByAccountNameContainingIgnoreCase(accountName);
    List<User> userList = new ArrayList<>();
    for (Account account : accountWrapper) {
      userList.add(account.getAccountUser());
    }
    return userList;
  }

  /**
   * Retrieve users by phone or email.
   * Param: email
   * Param: phone
   * Return: A list of users whose phone or email match the search string
   */
  @Override
  public List<User> getUserByEmailOrPhone(String email, String phone) {
    return userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(email, phone);
  }

  /**
   * Using geUserById to generate a search response for the controller.
   * Param: id
   * Return: Search response
   */
  @Override
  public ProfileRsp searchById(Long id) {
    //Getting user by id
    User user = getUserById(id);

    //Issue response
    return new ProfileRsp(user);
  }

  /**
   * Using getUserByName and getUserByEmailOrPhone
   * to generate a search response for the controller.
   * Param: userInfo
   * Return: Search response
   */
  @Override
  public ProfilesRsp searchByInfo(String userInfo) {

    List<User> userList;
    Set<Long> uids = new HashSet<>();

    userList = getUserByEmailOrPhone(userInfo, userInfo);
    userList.addAll(getUserByName(userInfo));

    //Compose profile list
    List<ProfileRsp> profileList = new ArrayList<>();
    for (User user : userList) {
      if (uids.contains(user.getId())) {
        continue;
      }
      ProfileRsp res = new ProfileRsp(user);
      profileList.add(res);
      uids.add(user.getId());
    }

    //Insert profile list into search response
    ProfilesRsp searchRes = new ProfilesRsp();
    searchRes.setUserProfiles(profileList);

    return searchRes;
  }
}
