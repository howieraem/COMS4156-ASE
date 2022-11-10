package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.LoginReq;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.security.CustomUserDetails;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final UserService userService;

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider jwtTokenProvider;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AuthController(
      UserService userService,
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
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
    registerReq.setPassword(passwordEncoder.encode(registerReq.getPassword()));
    return new ResponseEntity<>(userService.createUser(registerReq), HttpStatus.CREATED);
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
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginReq.getEmail(),
            loginReq.getPassword()
        )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String token = jwtTokenProvider.generateTokenFromUserId(userDetails.getId());
    return ResponseEntity.ok(token);
  }
}
