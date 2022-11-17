package com.lgtm.easymoney.security;

import com.lgtm.easymoney.services.impl.UserServiceImpl;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/** Process client jwt before the controller handles the request. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Value("${app.jwt.header}")
  private String tokenRequestHeader;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserServiceImpl userDetailsService;

  /**
   * Filter the incoming request for a valid token in the request header.
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain) throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);

    if (jwtTokenProvider.validateToken(jwt)) {
      String userName = jwtTokenProvider.getUsernameFromJwt(jwt);
      UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
      var authorities = userDetails.getAuthorities();
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, jwt, authorities);
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
    return StringUtils.hasText(token) ? token : "";
  }
}
