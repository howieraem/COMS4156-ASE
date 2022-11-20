package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.BalanceReq;
import com.lgtm.easymoney.payload.req.BizProfileReq;
import com.lgtm.easymoney.payload.rsp.BalanceRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user controller.
 */
@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "Authorization")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Get the full details of the current logged-in user.
   */
  @GetMapping("/me")
  @Operation(summary = "Method for the current logged-in user to view the full profile.")
  public ResponseEntity<User> getCurrentUser(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(principal.get(), HttpStatus.OK);
  }

  /**
   * Let a user deposit money to the balance (assume money
   * deducted from the bank account registered).
   */
  @PutMapping("/deposit")
  @Operation(summary = "Method for a user to "
          + "deposit money to this service from the bank account registered.")
  public ResponseEntity<BalanceRsp> deposit(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
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
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody BalanceReq req) {
    return new ResponseEntity<>(
        userService.makeWithdraw(principal.get(), req.getAmount()), HttpStatus.OK);
  }

  /**
   * Let a non-personal user update the business profile.
   */
  @PutMapping("/biz")
  @Operation(summary = "Method for a non-personal user to update the business profile.")
  public ResponseEntity<Void> updateBiz(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody BizProfileReq req) {
    userService.updateBizProfile(principal.get(), req);
    return ResponseEntity.ok().build();
  }
}
