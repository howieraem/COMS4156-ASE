package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.repositories.AccountRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.SearchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @Override
  public User getUserById(Long id) {
    var userWrapper = userRepository.findById(id);
    if (userWrapper.isEmpty()) {
      throw new ResourceNotFoundException("User", "id", id);
    }

    return userWrapper.get();
  }

  @Override
  public List<User> getUserByName(String accountName) {
    var accountWrapper = accountRepository.findByAccountNameContainingIgnoreCase(accountName);
    List<User> userList = new ArrayList<User>();
    for (Account account : accountWrapper) {
      userList.add(account.getAccountUser());
    }
    return userList;
  }

  @Override
  public List<User> getUserByEmailOrPhone(String email, String phone) {
    return userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(email, phone);
  }

  @Override
  public ResponseEntity<SearchRsp> searchById(Long id) {
    //Getting user by id
    User user = getUserById(id);
    //Compose response
    ProfileRsp res = new ProfileRsp();
    res.setAccountName(user.getAccount().getAccountName());
    res.setEmail(user.getEmail());
    res.setAddress(user.getAddress());
    res.setUserType(user.getType());
    res.setPhone(user.getPhone());

    List<ProfileRsp> profileList = new ArrayList<ProfileRsp>();
    boolean searchResult = true;
    profileList.add(res);
    SearchRsp searchRes = new SearchRsp();
    searchRes.setSuccess(searchResult);
    searchRes.setUserProfiles(profileList);

    //Issue response
    if (searchResult) {
      return ResponseEntity.status(HttpStatus.OK).body(searchRes);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchRes);
  }

  @Override
  public ResponseEntity<SearchRsp> searchByInfo(String userInfo) {

    List<User> userList;
    Set<Long> uids = new HashSet<>();

    userList = getUserByEmailOrPhone(userInfo, userInfo);
    userList.addAll(getUserByName(userInfo));

    //Successful search
    boolean searchResult = true;

    //Compose profile list
    List<ProfileRsp> profileList = new ArrayList<ProfileRsp>();
    for (User user : userList) {
      if (uids.contains(user.getId())) {
        continue;
      }
      ProfileRsp res = new ProfileRsp();
      res.setUid(user.getId());
      res.setAccountName(user.getAccount().getAccountName());
      res.setEmail(user.getEmail());
      res.setAddress(user.getAddress());
      res.setUserType(user.getType());
      res.setPhone(user.getPhone());
      profileList.add(res);
      uids.add(user.getId());
    }

    //Insert profile list into search response
    SearchRsp searchRes = new SearchRsp();
    searchRes.setSuccess(searchResult);
    searchRes.setUserProfiles(profileList);

    if (searchResult) {
      return ResponseEntity.status(HttpStatus.OK).body(searchRes);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchRes);
  }
}
