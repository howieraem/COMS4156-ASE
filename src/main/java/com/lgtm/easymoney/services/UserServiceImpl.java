package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public User getUserByID(Long id) {
        var userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) {
            return null;
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
    public boolean makeADeposit(User user, BigDecimal amount ) {
        if (user == null || amount == null) {
            return false;
        }
        var balance = user.getBalance();
        balance = balance.add(amount);
        user.setBalance(balance);
        userRepository.save(user);
        return true;
    }
    @Override
    public boolean makeAWithdraw(User user, BigDecimal amount ) {
        if (user == null || amount == null) {
            return false;
        }
        var balance = user.getBalance();
        if (balance.compareTo(amount) < 0) {
            return false;
        }
        balance = balance.subtract(amount);
        user.setBalance(balance);
        userRepository.save(user);
        return true;
    }

}
