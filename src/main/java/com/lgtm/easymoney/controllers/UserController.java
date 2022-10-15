package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody BalanceReq req) {
        return userService.makeADeposit(req);
    }
    @PutMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody BalanceReq req) {
        return userService.makeAWithdraw(req);
    }

}
