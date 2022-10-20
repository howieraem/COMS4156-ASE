package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Operation(description = "Method for a user to deposit money to this service from the bank account registered.")
    public ResponseEntity<BalanceRsp> deposit(@Valid @RequestBody BalanceReq req) {
        return new ResponseEntity<>(userService.makeADeposit(req), HttpStatus.OK);
    }
    @PutMapping("/withdraw")
    @Operation(description = "Method for a user to withdraw money from this service to the bank account registered.")
    public ResponseEntity<BalanceRsp> withdraw(@Valid @RequestBody BalanceReq req) {
        return new ResponseEntity<>(userService.makeAWithdraw(req), HttpStatus.OK);
    }

}
