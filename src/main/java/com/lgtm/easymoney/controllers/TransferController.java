package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Transfer controller.
 */
@RestController
@RequestMapping("/transfer")
@SecurityRequirement(name = "Authorization")
public class TransferController {
  private final TransferService transferService;

  @Autowired
  public TransferController(TransferService transferService) {
    this.transferService = transferService;
  }

  /**
   * Create a money transfer from one user to another.
   *
   * @param req transfer request
   * @return response entity with created transfer id
   */
  @PostMapping("/create")
  @Operation(summary = "Method for a user to create a money transfer to another user.")
  public ResponseEntity<ResourceCreatedRsp> transfer(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody TransferReq req) {
    return new ResponseEntity<>(
        transferService.makeTransfer(principal.get(), req), HttpStatus.CREATED);
  }

  /**
   * Get all transfers corresponding to a specific user.
   *
   * @param principal current logged-in user
   * @return response entity with list of transfers
   */
  @GetMapping
  @Operation(summary = "Method for a user to get"
          + " all money transfers (incl. completed money requests).")
  public ResponseEntity<TransferRsp> getTransfers(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    // get all the transfers (both from and to) corresponding to the user with given uid
    // TODO: isFromOrTo may be added to param as filter
    return new ResponseEntity<>(transferService.getTransfers(principal.get()), HttpStatus.OK);
  }
}
