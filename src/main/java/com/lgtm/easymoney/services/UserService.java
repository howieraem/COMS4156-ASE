package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.BizProfileReq;
import com.lgtm.easymoney.payload.rsp.BalanceRsp;
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

  BalanceRsp makeDeposit(User user, BigDecimal amount);

  BalanceRsp makeWithdraw(User user, BigDecimal amount);

  void updateBizProfile(User user, BizProfileReq req);
}
