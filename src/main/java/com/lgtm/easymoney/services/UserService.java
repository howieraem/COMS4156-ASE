package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * user service interface. restful api for accessing users.
 */
public interface UserService {
  boolean existsById(Long id);

  User getUserById(Long id);

  User saveUser(User user);

  List<User> getAllUsers();

  BalanceRsp makeDeposit(User user, BigDecimal amount);

  BalanceRsp makeWithdraw(User user, BigDecimal amount);
}
