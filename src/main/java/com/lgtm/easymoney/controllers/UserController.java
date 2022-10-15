package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.ErrorRsp;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/balance")
public class UserController {
    @Autowired
    private UserService userService;
    @PutMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody BalanceReq req) {
        // get params
        Long uid = req.getUid();
        BigDecimal amount = req.getAmount();
        // make a deposit
        User user = userService.getUserByID(uid);
        boolean success = userService.makeADeposit(user, amount);
        // payload
        BalanceRsp res = new BalanceRsp();
        res.setSuccess(success);
        res.setCurrBalance(user.getBalance());

        return success ? ResponseEntity.status(HttpStatus.OK).body(res) :
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorRsp(new ArrayList<>(Arrays.asList("i think we need to refactor this")), "Error in making a deposit."));

    }
    @PutMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody BalanceReq req) {
        // get params
        Long uid = req.getUid();
        BigDecimal amount = req.getAmount();
        // make a withdraw
        User user = userService.getUserByID(uid);
        boolean success = userService.makeAWithdraw(user, amount);
        // payload
        BalanceRsp res = new BalanceRsp();
        res.setSuccess(success);
        res.setCurrBalance(user.getBalance());

        return success ? ResponseEntity.status(HttpStatus.OK).body(res) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorRsp(new ArrayList<>(Arrays.asList("i think we need to refactor this")), "Error in making a withdraw."));

    }

//    @PutMapping("/update")
//    public ResponseEntity<?> update(@Valid @RequestBody BalanceReq balanceReq) {
//        var userWrapper = userRepository.findById(balanceReq.getUid());
//        if (userWrapper.isEmpty()) {
//            List<String> errorFields = new ArrayList<>();
//            errorFields.add("uid");
//            // TODO load the current authenticated user, no need to check existence
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorRsp(errorFields, "User not found!"));
//        }
//
//        // TODO maybe better to move the logics below to service layer?
//        var user = userWrapper.get();
//        var balance = user.getBalance();
//        var amount = balanceReq.getAmount();
//        var rsp = new BalanceRsp();
//
//        if (balanceReq.getIsDeposit()) {
//            balance = balance.add(amount);
//            user.setBalance(balance);
//            rsp.setSuccess(Boolean.TRUE);
//        } else {
//            if (balance.compareTo(amount) < 0) {
//                rsp.setSuccess(Boolean.FALSE);
//            } else {
//                balance = balance.subtract(amount);
//                user.setBalance(balance);
//                rsp.setSuccess(Boolean.TRUE);
//            }
//        }
//
//        userRepository.save(user);
//        rsp.setCurrBalance(balance);
//
//        return ResponseEntity.status(HttpStatus.OK).body(rsp);
//    }
}
