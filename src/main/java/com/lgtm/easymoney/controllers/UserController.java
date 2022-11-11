package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.BalanceReq;
import com.lgtm.easymoney.payload.rsp.BalanceRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller.
 */
@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Let a user deposit money to the balance (assume money
   * deducted from the bank account registered).
   */
  @PutMapping("/deposit")
  @Operation(summary = "Method for a user to "
          + "deposit money to this service from the bank account registered.")
  public ResponseEntity<BalanceRsp> deposit(
      @CurrentUser UserPrincipal principal,
      @Valid @RequestBody BalanceReq req) {
    return new ResponseEntity<>(
        userService.makeDeposit(principal.get(), req.getAmount()), HttpStatus.OK);
  }

  /**
   * Let a user withdraw money from the balance (assume money
   * added to the bank account registered).
   */
  @PutMapping("/withdraw")
  @Operation(summary = "Method for a user to"
          + " withdraw money from this service to the bank account registered.")
  public ResponseEntity<BalanceRsp> withdraw(
      @CurrentUser UserPrincipal principal,
      @Valid @RequestBody BalanceReq req) {
    return new ResponseEntity<>(
        userService.makeWithdraw(principal.get(), req.getAmount()), HttpStatus.OK);
  }

}
