package com.lgtm.easymoney.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/** Configure the actions whenever an unauthenticated client tries to access private resources. */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final HandlerExceptionResolver resolver;

  @Autowired
  public JwtAuthenticationEntryPoint(
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse httpServletResponse,
      AuthenticationException ex) throws IOException {
    if (request.getAttribute("javax.servlet.error.exception") != null) {
      Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
      resolver.resolveException(request, httpServletResponse, null, (Exception) throwable);
    }
    if (!httpServletResponse.isCommitted()) {
      httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
    }
  }
}
