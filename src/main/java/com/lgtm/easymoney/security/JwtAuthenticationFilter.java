package com.lgtm.easymoney.security;

import com.lgtm.easymoney.services.UserService;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Value("${app.jwt.header}")
  private String tokenRequestHeader;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private JwtTokenValidator jwtTokenValidator;

  @Autowired
  private UserService customUserDetailsService;

  /**
   * Filter the incoming request for a valid token in the request header.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);

    if (StringUtils.hasText(jwt) && jwtTokenValidator.validateToken(jwt)) {
      Long userId = jwtTokenProvider.getUserIdFromJwt(jwt);
      UserDetails userDetails = customUserDetailsService.loadUserById(userId);
      List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromJwt(jwt);
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, jwt, authorities);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract the token from the Authorization request header.
   */
  private String getJwtFromRequest(HttpServletRequest request) {
    String token = request.getHeader(tokenRequestHeader);
    return StringUtils.hasText(token) ? token : null;
  }
}
