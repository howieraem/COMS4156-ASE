package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByID(Long id) {
        return userRepository.existsById(id);
    }
    @Override
    public User getUserByID(Long id) {
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
    public boolean makeADeposit(User user, BigDecimal amount) {
//        if (user == null || amount == null) {
//            return false;
//        }
        var balance = user.getBalance();
        balance = balance.add(amount);
        user.setBalance(balance);
        userRepository.save(user);
        return true;
    }
    @Override
    public boolean makeAWithdraw(User user, BigDecimal amount) {
//        if (user == null || amount == null) {
//            return false;
//        }
        var balance = user.getBalance();
        if (balance.compareTo(amount) < 0) {
            throw new InvalidUpdateException("User", user.getId(), "balance", amount);
        }
        balance = balance.subtract(amount);
        user.setBalance(balance);
        userRepository.save(user);
        return true;
    }
    @Override
    public BalanceRsp makeADeposit(BalanceReq req) {
        // get params
        Long uid = req.getUid();
        BigDecimal amount = req.getAmount();
        // make a deposit
        User user = getUserByID(uid);
        boolean success = makeADeposit(user, amount);
        // payload
        BalanceRsp res = new BalanceRsp();
        res.setSuccess(success);
        res.setCurrBalance(user.getBalance());
        return res;
    }
    @Override
    public BalanceRsp makeAWithdraw(BalanceReq req) {
        // get params
        Long uid = req.getUid();
        BigDecimal amount = req.getAmount();
        // make a withdraw
        User user = getUserByID(uid);
        boolean success = makeAWithdraw(user, amount);
        // payload
        BalanceRsp res = new BalanceRsp();
        res.setSuccess(success);
        res.setCurrBalance(user.getBalance());
        return res;
    }
}
