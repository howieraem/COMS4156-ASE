package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.LoanRsp;
import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class LoanController {
  private final LoanService loanService;

  @Autowired
  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  /**
   * Create a loan request.
   *
   * @param req loan request
   * @return response entity with created loan request id
   */
  @PostMapping("/request")
  @Operation(summary = "Method for a personal user to create a loan request")
  public ResponseEntity<ResourceCreatedRsp> requestLoan(@Valid @RequestBody RequestReq req) {
    return new ResponseEntity<>(loanService.requestLoan(req), HttpStatus.CREATED);
  }

  /**
   * Get all loans corresponding to a specific user.
   *
   * @param uid user's id
   * @return response entity with list of loans
   */
  @GetMapping("/{uid}")
  @Operation(summary = "Method for a user to get all loans")
  public ResponseEntity<LoanRsp> getLoans(@PathVariable(value = "uid") Long uid) {
    return new ResponseEntity<>(loanService.getLoansByUid(uid), HttpStatus.OK);
  }

  /**
   * Approve a loan request.
   *
   * @param req request payload, contains loan id, lender id, borrower id
   * @return response entity of approved loan and automatically created loan-payback request
   */
  @PutMapping("/approve")
  @Operation(summary = "Method for a financial user to approve a loan request")
  public ResponseEntity<LoanRsp> approveLoan(@Valid @RequestBody RequestAcceptDeclineReq req) {
    return new ResponseEntity<>(loanService.approveLoan(req), HttpStatus.OK);
  }

  /**
   * Decline a loan request.
   *
   * @param req request payload, contains loan id, lender id, borrower id
   * @return response entity of declined loan id
   */
  @PutMapping("/decline")
  @Operation(summary = "Method for a financial user to decline a loan request")
  public ResponseEntity<LoanRsp> declineLoan(
      @Valid @RequestBody RequestAcceptDeclineReq req) {
    return new ResponseEntity<>(loanService.declineLoan(req), HttpStatus.OK);
  }
}