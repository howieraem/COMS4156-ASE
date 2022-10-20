package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller for authentication.
 */
@RestController
@RequestMapping("/user")
public class AuthController {
  private final UserService userService;

  @Autowired
  public AuthController(UserService userService) {
    this.userService = userService;
  }

  /**
   * handle register requests.
   *
   * @param registerReq reg request with user's info.
   * @return response entity of the new user's ID if succeeded or error information if failed
   */
  @PostMapping("/register")
  @Operation(summary = "Method for new user registration.")
  public ResponseEntity<ResourceCreatedRsp> register(@Valid @RequestBody RegisterReq registerReq) {
    return new ResponseEntity<>(userService.createUser(registerReq), HttpStatus.CREATED);
  }

  // TODO login
}
