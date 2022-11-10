package com.lgtm.easymoney.security;

import com.lgtm.easymoney.exceptions.InvalidTokenRequestException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtTokenValidator {
  private final String jwtSecret;

  @Autowired
  public JwtTokenValidator(@Value("${app.jwt.secret}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  /**
   * Validates if a token satisfies the following properties.
   * - Signature is not malformed
   * - Token hasn't expired
   * - Token is supported
   */
  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
    } catch (RuntimeException ex) {
      throw new InvalidTokenRequestException("Malformed, expired or unsupported token", authToken);
    }
    return true;
  }
}
