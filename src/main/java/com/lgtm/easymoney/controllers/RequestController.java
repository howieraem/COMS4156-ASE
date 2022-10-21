package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
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
 * controller for handling requets.
 */
@RestController
@RequestMapping("/request")
public class RequestController {
  private final RequestService requestService;

  @Autowired
  public RequestController(RequestService requestService) {
    this.requestService = requestService;
  }

  @PostMapping("/create")
  @Operation(summary = "Method for a user to create a money request to another user.")
  public ResponseEntity<ResourceCreatedRsp> createRequest(@Valid @RequestBody RequestReq req) {
    return new ResponseEntity<>(requestService.createRequest(req), HttpStatus.CREATED);
  }

  /**
   * get all requests sent/received by user.
   *
   * @param uid user's id
   * @return response entity with list of requests.
   */
  @GetMapping("/{uid}")
  @Operation(summary = "Method for a user "
          + "to get all money requests sent (but not yet completed).")
  public ResponseEntity<RequestRsp> getRequests(@PathVariable(value = "uid") Long uid) {
    return new ResponseEntity<>(requestService.getRequestsByUid(uid), HttpStatus.OK);
  }


  /**
   * accept a request.
   *
   * @param r request payload, contains request id, sender id, receiver id.
   * @return response entity of accepted request id if successful and exceptions otherwise.
   */

  @PutMapping("/accept")
  @Operation(summary = "Method for a user to accept a money request from another user.")
  public ResponseEntity<ResourceCreatedRsp> acceptRequest(
          @Valid @RequestBody RequestAcceptDeclineReq r) {
    return new ResponseEntity<>(requestService.acceptRequest(
            r.getRequestid(),
            r.getFromUid(),
            r.getToUid()), HttpStatus.OK);
  }

  /**
   * decline a request.
   *
   * @param r request payload, contains request id, sender id, receiver id.
   * @return response entity of declined request id if successful and exceptions otherwise.
   */
  @PutMapping("/decline")
  @Operation(summary = "Method for a user to decline a money request from another user.")
  public ResponseEntity<ResourceCreatedRsp> declineRequest(
          @Valid @RequestBody RequestAcceptDeclineReq r) {
    return new ResponseEntity<>(requestService.declineRequest(
            r.getRequestid(),
            r.getFromUid(),
            r.getToUid()), HttpStatus.OK);
  }

}
