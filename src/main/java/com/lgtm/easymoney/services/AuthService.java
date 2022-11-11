package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.req.LoginReq;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;

/** Auth service interface. */
public interface AuthService {
  ResourceCreatedRsp register(RegisterReq registerReq);

  String login(LoginReq loginReq);
}
