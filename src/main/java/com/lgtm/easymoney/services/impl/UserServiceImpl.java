package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.BizProfile;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * user service implementation. restful apis for accessing users.
 */
@Service("userService")
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

  /**
   * create a user.
   *
   * @param registerReq request of register info
   * @return uid if createed successfully.
   */
  @Override
  public ResourceCreatedRsp createUser(RegisterReq registerReq) {
    return new ResourceCreatedRsp(saveUser(buildUser(registerReq)).getId());
  }

  /**
   * build user given register request.
   *
   * @param registerReq request.
   * @return user
   */
  public User buildUser(RegisterReq registerReq) {
    var user = new User();
    user.setEmail(registerReq.getEmail());
    user.setPassword(registerReq.getPassword());  // TODO spring security password encoding
    user.setTypeByStr(registerReq.getUserType());
    user.setPhone(registerReq.getPhone());
    user.setAddress(registerReq.getAddress());

    var account = new Account();
    account.setAccountName(registerReq.getAccountName());
    account.setAccountNumber(registerReq.getAccountNumber());
    account.setRoutingNumber(registerReq.getRoutingNumber());
    user.setAccount(account);

    if (user.getType() != UserType.PERSONAL) {
      BizProfile bizProfile = new BizProfile();
      bizProfile.setPromotionText(registerReq.getBizPromotionText());
      user.setBizProfile(bizProfile);
      bizProfile.setBizUser(user);
    }
    return user;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean makeDeposit(User user, BigDecimal amount) {
    var balance = user.getBalance();
    balance = balance.add(amount);
    user.setBalance(balance);
    saveUser(user);
    return true;
  }

  @Override
  public BalanceRsp makeDeposit(BalanceReq req) {
    // get params
    Long uid = req.getUid();
    BigDecimal amount = req.getAmount();
    // make a deposit
    User user = getUserById(uid);
    makeDeposit(user, amount);
    // payload
    return new BalanceRsp(user.getBalance());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean makeWithdraw(User user, BigDecimal amount) {
    var balance = user.getBalance();
    if (balance.compareTo(amount) < 0) {
      // Not enough balance
      throw new InvalidUpdateException("User", user.getId(), "amount", amount);
    }
    balance = balance.subtract(amount);
    user.setBalance(balance);
    saveUser(user);
    return true;
  }

  @Override
  public BalanceRsp makeWithdraw(BalanceReq req) {
    // get params
    Long uid = req.getUid();
    BigDecimal amount = req.getAmount();
    // make a withdraw
    User user = getUserById(uid);
    makeWithdraw(user, amount);
    // payload
    return new BalanceRsp(user.getBalance());
  }


}
