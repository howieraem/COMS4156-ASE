package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import com.lgtm.easymoney.services.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Transfer controller.
 */
@RestController
@RequestMapping("/transfer")
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
  public ResponseEntity<ResourceCreatedRsp> transfer(@Valid @RequestBody TransferReq req) {
    return new ResponseEntity<>(transferService.makeTransfer(req), HttpStatus.CREATED);
  }

  /**
   * Get all transfers corresponding to a specific user.
   *
   * @param uid user's id
   * @return response entity with list of transfers
   */
  @GetMapping("/{uid}")
  @Operation(summary = "Method for a user to get"
          + " all money transfers (incl. completed money requests).")
  public ResponseEntity<TransferRsp> getTransfers(@PathVariable(value = "uid") Long uid) {
    // get all the transfers (both from and to) corresponding to the user with given uid
    // TODO: isFromOtTo may be added to param as filter
    return new ResponseEntity<>(transferService.getTransfersByUid(uid), HttpStatus.OK);
  }
}
