package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.RequestRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.RequestService;
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
 * controller for handling money requests.
 */
@RestController
@RequestMapping("/request")
@SecurityRequirement(name = "Authorization")
public class RequestController {
  private final RequestService requestService;

  // initialize constructor with requestService.
  @Autowired
  public RequestController(RequestService requestService) {
    this.requestService = requestService;
  }

  /**
   * create a new money request between two users.
   *
   * @param principal current logged-in user.
   * @param req request payload.
   * @return response with request id
   */
  @PostMapping("/create")
  @Operation(summary = "Method for a user to create a money request to another user.")
  public ResponseEntity<ResourceCreatedRsp> createRequest(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestReq req) {
    return new ResponseEntity<>(
        requestService.createRequest(principal.get(), req), HttpStatus.CREATED);
  }

  /**
   * get all requests sent/received by user.
   *
   * @param principal current logged-in user.
   * @return response entity with list of requests.
   */
  @GetMapping
  @Operation(summary = "Method for a user "
          + "to get all money requests sent (but not yet completed).")
  public ResponseEntity<RequestRsp> getRequests(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(requestService.getRequests(principal.get()), HttpStatus.OK);
  }


  /**
   * accept a request.
   *
   * @param principal current logged-in user.
   * @param r request payload, contains request id, sender id, receiver id.
   * @return response entity of accepted request id if successful and exceptions otherwise.
   */
  @PutMapping("/accept")
  @Operation(summary = "Method for a user to accept a money request from another user.")
  public ResponseEntity<ResourceCreatedRsp> acceptRequest(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestAcceptDeclineReq r) {
    return new ResponseEntity<>(requestService.acceptRequest(
            r.getRequestid(),
            principal.getId(),
            r.getToUid()), HttpStatus.OK);
  }

  /**
   * decline a request.
   *
   * @param principal current logged-in user.
   * @param r request payload, contains request id, sender id, receiver id.
   * @return response entity of declined request id if successful and exceptions otherwise.
   */
  @PutMapping("/decline")
  @Operation(summary = "Method for a user to decline a money request from another user.")
  public ResponseEntity<ResourceCreatedRsp> declineRequest(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody RequestAcceptDeclineReq r) {
    return new ResponseEntity<>(requestService.declineRequest(
            r.getRequestid(),
            principal.getId(),
            r.getToUid()), HttpStatus.OK);
  }

}
