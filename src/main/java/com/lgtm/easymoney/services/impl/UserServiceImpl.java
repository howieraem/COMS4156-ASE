package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.BizProfileReq;
import com.lgtm.easymoney.payload.rsp.BalanceRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service implementation, containing logics of CRUD of users.
 */
@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {
  private final UserRepository userRepository;

  /**
   * Constructor of user service.
   *
   * @param userRepository JPA repository to perform CRUD in user table.
   */
  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /** Whether a user with id exists in the user table. */
  @Override
  public boolean existsById(Long id) {
    return userRepository.existsById(id);
  }

  /** Retrieve the user with id if exists, otherwise throws a ResourceNotFound exception. */
  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
  }

  /** Save the user to the user table, which may be invoked by create or update. */
  @Override
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /** Get all users in the user table (for internal use only). */
  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Deposit the specified amount of money to the user's balance
   * (assume money is deducted from the registered bank account).
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public BalanceRsp makeDeposit(User user, BigDecimal amount) {
    var balance = user.getBalance();
    balance = balance.add(amount);
    user.setBalance(balance);
    saveUser(user);
    return new BalanceRsp(user.getBalance());
  }

  /**
   * Withdraw the specified amount of money from the user's balance.
   * (assume money is added to the registered bank account).
   * Throw InvalidUpdateException if not enough balance.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public BalanceRsp makeWithdraw(User user, BigDecimal amount) {
    var balance = user.getBalance();
    if (balance.compareTo(amount) < 0) {
      // Not enough balance
      throw new InvalidUpdateException("User", user.getId(), "amount", amount);
    }
    balance = balance.subtract(amount);
    user.setBalance(balance);
    saveUser(user);
    return new BalanceRsp(user.getBalance());
  }

  /**
   * Load current logged-in user from email extracted from jwt.
   *
   * @param email user's email.
   * @return details of the logged-in principal.
   * @throws UsernameNotFoundException if email doesn't exist.
   */
  @Transactional
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var usr = userRepository.findByEmail(email);
    if (usr == null) {
      throw new UsernameNotFoundException(email);
    }
    Hibernate.initialize(usr.getAccount());
    Hibernate.initialize(usr.getFriendships());
    Hibernate.initialize(usr.getGroups());
    Hibernate.initialize(usr.getTransactionsSent());
    Hibernate.initialize(usr.getTransactionsReceived());
    return new UserPrincipal(usr);
  }

  /**
   * Update business profile for non-personal users.
   *
   * @param user current logged-in user
   * @param req payload containing new info
   */
  @Transactional
  @Override
  public void updateBizProfile(User user, BizProfileReq req) {
    if (user.getType() == UserType.PERSONAL) {
      throw new InapplicableOperationException(
          "user", user.getId(), "Authorization", "updateBizProfile");
    }
    user.setBizPromotionText(req.getPromotionText());
    saveUser(user);
  }
}
