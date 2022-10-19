package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    boolean existsByID(Long id);
    User getUserByID(Long id);
    User saveUser(User user);
    List<User> getAllUsers();
    ResourceCreatedRsp createUser(RegisterReq registerReq);
    boolean makeADeposit(User user, BigDecimal amount);
    boolean makeAWithdraw(User user, BigDecimal amount);
    BalanceRsp makeADeposit(BalanceReq req);
    BalanceRsp makeAWithdraw(BalanceReq req);
}
