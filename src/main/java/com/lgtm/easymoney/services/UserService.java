package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User getUserByID(Long id);
    User saveUser(User user);
    List<User> getAllUsers();
    boolean makeADeposit(User user, BigDecimal amount);
    boolean makeAWithdraw(User user, BigDecimal amount);
    ResponseEntity<BalanceRsp> makeADeposit(BalanceReq req);
    ResponseEntity<BalanceRsp> makeAWithdraw(BalanceReq req);
}
