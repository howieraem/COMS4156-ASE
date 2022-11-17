package com.lgtm.easymoney.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Generates jwt given user info and expiration time. */
@Component
public class JwtTokenProvider {
  private final String jwtSecret;
  private final long jwtExpirationInMs;

  public JwtTokenProvider(
      @Value("${app.jwt.secret}") String jwtSecret,
      @Value("${app.jwt.expiration}") long jwtExpirationInMs) {
    this.jwtSecret = jwtSecret;
    this.jwtExpirationInMs = jwtExpirationInMs;
  }

  /**
   * Generates a token from a principal object. Embed the refresh token in the jwt
   * so that a new jwt can be created
   */
  public String generateToken(String email) {
    Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(Date.from(expiryDate))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * Returns the user id encapsulated within the token.
   */
  public String getUsernameFromJwt(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  /**
   * Validates if a token satisfies the following properties.
   * - Signature is not malformed
   * - Token hasn't expired
   * - Token is supported
   */
  public boolean validateToken(String authToken) {
    if (authToken.isEmpty()) {
      return false;
    }
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
    } catch (RuntimeException ex) {
      return false;
    }
    return true;
  }

}
