package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.ErrorRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.RegisterRsp;
import com.lgtm.easymoney.repositories.AccountRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UserController(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq registerReq, Errors errors) {
        if (userRepository.existsByEmail(registerReq.getEmail())) {
            List<String> errorFields = new ArrayList<>();
            errorFields.add("email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorRsp(errorFields, "Email already registered!"));
        }
        if (accountRepository.existsByNumberAndRoutingNumber(registerReq.getAccountNumber(), registerReq.getRoutingNumber())) {
            List<String> errorFields = new ArrayList<>();
            errorFields.add("accountNumber");
            errorFields.add("rountingNumber");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorRsp(errorFields, "Bank account already registered!"));
        }

        var user = new User();
        user.setEmail(registerReq.getEmail());
        user.setPassword(registerReq.getPassword());  // TODO spring security password encoding
        user.setTypeByStr(registerReq.getUserType());
        user.setPhone(registerReq.getPhone());
        user.setAddress(registerReq.getAddress());

        var account = new Account();
        account.setName(registerReq.getAccountName());
        account.setNumber(registerReq.getAccountNumber());
        account.setRoutingNumber(registerReq.getRoutingNumber());
        user.setAccount(account);

        var userCreated = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterRsp(userCreated.getId()));
    }

    // TODO login, get profile, update profile
}
