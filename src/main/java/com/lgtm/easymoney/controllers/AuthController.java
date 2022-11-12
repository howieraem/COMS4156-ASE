package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.LoginReq;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.services.AuthService;
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
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * handle register requests.
   *
   * @param registerReq register request with new user's info.
   * @return response entity of the new user's id if succeeded, or error information if failed
   */
  @PostMapping("/register")
  @Operation(summary = "Method for new user registration.")
  public ResponseEntity<ResourceCreatedRsp> register(@Valid @RequestBody RegisterReq registerReq) {
    return new ResponseEntity<>(authService.register(registerReq), HttpStatus.CREATED);
  }

  /**
   * handle login requests.
   *
   * @param loginReq login request with email and password
   * @return JWT
   */
  @PostMapping("/login")
  @Operation(summary = "Method for user login.")
  public ResponseEntity<String> login(@Valid @RequestBody LoginReq loginReq) {
    return ResponseEntity.ok(authService.login(loginReq));
  }
}
