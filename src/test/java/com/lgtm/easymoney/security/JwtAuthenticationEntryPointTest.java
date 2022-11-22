package com.lgtm.easymoney.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringRunner;

/** Test the server action with/without authentication exception. */
@RunWith(SpringRunner.class)
public class JwtAuthenticationEntryPointTest {
  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Before
  public void setUp() {
    jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
  }

  @Test
  public void testAuthenticated() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    response.setCommitted(true);

    jwtAuthenticationEntryPoint.commence(request, response, null);

    assertNotEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
  }

  @Test
  public void testUnauthenticated() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AuthenticationException ex = new AuthenticationCredentialsNotFoundException("");

    jwtAuthenticationEntryPoint.commence(request, response, ex);

    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
  }
}
