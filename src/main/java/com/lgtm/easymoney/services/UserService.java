package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User getUserByID(Long id);
    User saveUser(User user);
    List<User> getAllUsers();
    boolean makeADeposit(User user, BigDecimal amount);
    boolean makeAWithdraw(User user, BigDecimal amount);
}
