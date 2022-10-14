package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/balance")
public class BalanceController {
    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody BalanceReq balanceReq) {
        var userWrapper = userRepository.findById(balanceReq.getUid());
        if (userWrapper.isEmpty()) {
            // TODO load the current authenticated user, no need to check existence
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }

        // TODO maybe better to move the logics below to service layer?
        var user = userWrapper.get();
        var balance = user.getBalance();
        var amount = balanceReq.getAmount();
        var rsp = new BalanceRsp();

        if (balanceReq.getIsDeposit()) {
            balance = balance.add(amount);
            user.setBalance(balance);
            rsp.setSuccess(Boolean.TRUE);
        } else {
            if (balance.compareTo(amount) < 0) {
                rsp.setSuccess(Boolean.FALSE);
            } else {
                balance = balance.subtract(amount);
                user.setBalance(balance);
                rsp.setSuccess(Boolean.TRUE);
            }
        }

        userRepository.save(user);
        rsp.setCurrBalance(balance);

        return ResponseEntity.status(HttpStatus.OK).body(rsp);
    }
}
