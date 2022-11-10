package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.LoginReq;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;


public interface AuthService {
  ResourceCreatedRsp register(RegisterReq registerReq);

  String login(LoginReq loginReq);
}
