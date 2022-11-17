package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.LoginReq;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.AuthService;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Implementation of auth service. */
@Service
public class AuthServiceImpl implements AuthService {
  private final UserService userService;

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider jwtTokenProvider;

  private final PasswordEncoder passwordEncoder;

  /** Constructor of auth service. */
  @Autowired
  public AuthServiceImpl(
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
   * create a user.
   *
   * @param registerReq request of register info.
   * @return response with uid if created successfully.
   */
  @Override
  public ResourceCreatedRsp register(RegisterReq registerReq) {
    var user = new User();
    user.setEmail(registerReq.getEmail());
    user.setPassword(passwordEncoder.encode(registerReq.getPassword()));
    user.setTypeByStr(registerReq.getUserType());
    user.setPhone(registerReq.getPhone());
    user.setAddress(registerReq.getAddress());

    var account = new Account();
    account.setAccountName(registerReq.getAccountName());
    account.setAccountNumber(registerReq.getAccountNumber());
    account.setRoutingNumber(registerReq.getRoutingNumber());
    user.setAccount(account);

    if (user.getType() != UserType.PERSONAL) {
      user.setBizPromotionText(registerReq.getBizPromotionText());
    }

    return new ResourceCreatedRsp(userService.saveUser(user).getId());
  }

  @Override
  public String login(LoginReq loginReq) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginReq.getEmail(),
            loginReq.getPassword()
        )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
    return jwtTokenProvider.generateToken(userDetails.getUsername());
  }
}
