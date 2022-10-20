package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * user service implementation. restful apis for accessing users.
 */
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean existsById(Long id) {
    return userRepository.existsById(id);
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
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public boolean makeDeposit(User user, BigDecimal amount) {
    var balance = user.getBalance();
    balance = balance.add(amount);
    user.setBalance(balance);
    userRepository.save(user);
    return true;
  }

  @Override
  public ResponseEntity<BalanceRsp> makeDeposit(BalanceReq req) {
    // get params
    Long uid = req.getUid();
    BigDecimal amount = req.getAmount();
    // make a deposit
    User user = getUserById(uid);
    boolean success = makeDeposit(user, amount);
    // payload
    BalanceRsp res = new BalanceRsp();
    res.setSuccess(success);
    res.setCurrBalance(user.getBalance());
    if (success) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @Override
  public boolean makeWithdraw(User user, BigDecimal amount) {
    var balance = user.getBalance();
    if (balance.compareTo(amount) < 0) {
      return false;
    }
    balance = balance.subtract(amount);
    user.setBalance(balance);
    userRepository.save(user);
    return true;
  }



  @Override
  public ResponseEntity<BalanceRsp> makeWithdraw(BalanceReq req) {
    // get params
    Long uid = req.getUid();
    BigDecimal amount = req.getAmount();
    // make a withdraw
    User user = getUserById(uid);
    boolean success = makeWithdraw(user, amount);
    // payload
    BalanceRsp res = new BalanceRsp();
    res.setSuccess(success);
    res.setCurrBalance(user.getBalance());
    if (success) {
      return ResponseEntity.status(HttpStatus.OK).body(res);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }
}
