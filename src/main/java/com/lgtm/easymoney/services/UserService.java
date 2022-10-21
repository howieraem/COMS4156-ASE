package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import java.math.BigDecimal;
import java.util.List;

/**
 * user service interface. restful api for accessing users.
 */
public interface UserService {
  boolean existsById(Long id);

  User getUserById(Long id);

  User saveUser(User user);

  List<User> getAllUsers();

  ResourceCreatedRsp createUser(RegisterReq registerReq);

  boolean makeDeposit(User user, BigDecimal amount);

  BalanceRsp makeDeposit(BalanceReq req);

  BalanceRsp makeWithdraw(BalanceReq req);

  boolean makeWithdraw(User user, BigDecimal amount);

  

}
