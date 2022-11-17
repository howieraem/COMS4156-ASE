package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling loans.
 */
@RestController
@RequestMapping("/loan")
@SecurityRequirement(name = "Authorization")
public class LoanController {
  private final LoanService loanService;

  @Autowired
  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  /**
   * Create a loan request.
   *
   * @param principal current logged-in user
   * @param req loan request
   * @return response entity with created loan request id
   */
  @PostMapping("/request")
  @Operation(summary = "Method for a personal user to create a loan request")
  public ResponseEntity<ResourceCreatedRsp> requestLoan(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestReq req) {
    return new ResponseEntity<>(
        loanService.requestLoan(principal.get(), req), HttpStatus.CREATED);
  }

  /**
   * Get all loans corresponding to a specific user.
   *
   * @param principal current logged-in user
   * @return response entity with list of loans
   */
  @GetMapping
  @Operation(summary = "Method for a user to get all loans")
  public ResponseEntity<LoanRsp> getLoans(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(loanService.getLoansByUser(principal.get()), HttpStatus.OK);
  }

  /**
   * Approve a loan request.
   *
   * @param req request payload, contains loan id, lender id, borrower id
   * @return response entity of approved loan and automatically created loan-payback request
   */
  @PutMapping("/approve")
  @Operation(summary = "Method for a financial user to approve a loan request")
  public ResponseEntity<LoanRsp> approveLoan(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestAcceptDeclineReq req) {
    return new ResponseEntity<>(
        loanService.approveLoan(principal.get(), req), HttpStatus.OK);
  }

  /**
   * Decline a loan request.
   *
   * @param req request payload, contains loan id, lender id, borrower id
   * @return response entity of declined loan
   */
  @PutMapping("/decline")
  @Operation(summary = "Method for a financial user to decline a loan request")
  public ResponseEntity<LoanRsp> declineLoan(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestAcceptDeclineReq req) {
    return new ResponseEntity<>(
        loanService.declineLoan(principal.get(), req), HttpStatus.OK);
  }
}