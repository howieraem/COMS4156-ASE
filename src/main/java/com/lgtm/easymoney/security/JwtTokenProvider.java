package com.lgtm.easymoney.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private static final String AUTHORITIES_CLAIM = "authorities";
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
   * Return the jwt authorities claim encapsulated within the token.
   */
  public List<GrantedAuthority> getAuthoritiesFromJwt(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(jwtSecret)
        .parseClaimsJws(token)
        .getBody();
    return Arrays.stream(claims.get(AUTHORITIES_CLAIM).toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  /**
   * Private helper method to extract user authorities.
   */
  private String getUserAuthorities(CustomUserDetails customUserDetails) {
    return customUserDetails
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }

}
