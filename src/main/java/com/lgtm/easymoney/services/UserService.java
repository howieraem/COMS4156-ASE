package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * user service interface. restful api for accessing users.
 */
public interface UserService {
  boolean existsById(Long id);

  User getUserById(Long id);

  User saveUser(User user);

  List<User> getAllUsers();

  boolean makeDeposit(User user, BigDecimal amount);

  ResponseEntity<BalanceRsp> makeDeposit(BalanceReq req);

  boolean makeWithdraw(User user, BigDecimal amount);

  ResponseEntity<BalanceRsp> makeWithdraw(BalanceReq req);

}
