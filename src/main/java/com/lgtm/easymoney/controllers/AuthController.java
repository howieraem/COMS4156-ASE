package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.RegisterRsp;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterRsp> register(@Valid @RequestBody RegisterReq registerReq) {
        var user = new User();
        user.setEmail(registerReq.getEmail());
        user.setPassword(registerReq.getPassword());  // TODO spring security password encoding
        user.setTypeByStr(registerReq.getUserType());
        user.setPhone(registerReq.getPhone());
        user.setAddress(registerReq.getAddress());

        var account = new Account();
        account.setAccountName(registerReq.getAccountName());
        account.setAccountNumber(registerReq.getAccountNumber());
        account.setRoutingNumber(registerReq.getRoutingNumber());
        user.setAccount(account);

        var userCreated = userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterRsp(userCreated.getId()));
    }

    // TODO login
}
