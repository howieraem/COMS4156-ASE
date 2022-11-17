package com.lgtm.easymoney.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** Configure the actions whenever an unauthenticated client tries to access private resources. */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse httpServletResponse,
      AuthenticationException ex) throws IOException {
    if (!httpServletResponse.isCommitted()) {
      httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
    }
  }
}
