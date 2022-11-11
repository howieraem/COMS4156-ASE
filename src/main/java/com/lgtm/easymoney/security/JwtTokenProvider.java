package com.lgtm.easymoney.security;

import com.lgtm.easymoney.exceptions.InvalidTokenRequestException;
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
  public String generateTokenFromUserId(Long userId) {
    Instant expiryDate = Instant.now().plusMillis(jwtExpirationInMs);
    return Jwts.builder()
        .setSubject(Long.toString(userId))
        .setIssuedAt(new Date())
        .setExpiration(Date.from(expiryDate))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  /**
   * Returns the user id encapsulated within the token.
   */
  public Long getUserIdFromJwt(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();

    return Long.parseLong(claims.getSubject());
  }

  /**
   * Returns the token expiration date encapsulated within the token.
   */
  public Date getTokenExpiryFromJwt(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();

    return claims.getExpiration();
  }

  /**
   * Return the jwt expiration for the client so that they can execute
   * the refresh token logic appropriately.
   */
  public long getExpiryDuration() {
    return jwtExpirationInMs;
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
