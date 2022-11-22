package com.lgtm.easymoney.configs;

import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtAuthenticationFilter;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Security configurations of Spring MVC. */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig {
  private final UserServiceImpl userDetailsService;
  private final JwtAuthenticationEntryPoint jwtEntryPoint;

  @Autowired
  public WebSecurityConfig(UserServiceImpl userDetailsService,
                           JwtAuthenticationEntryPoint jwtEntryPoint) {
    this.userDetailsService = userDetailsService;
    this.jwtEntryPoint = jwtEntryPoint;
  }

  /**
   * Configure the authentication manager to load user with username/email
   * and verify the encoded password.
   */
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder())
        .and()
        .build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  /** Migrated from WebSecurityConfigurerAdapter.configure(WebSecurity web). */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring()
        .antMatchers("/v2/api-docs",
            "/v3/api-docs",
            "/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/webjars/**");
  }

  /** Migrated from WebSecurityConfigurerAdapter.configure(HttpSecurity http). */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf().disable()
        .exceptionHandling().authenticationEntryPoint(jwtEntryPoint)
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/",
            "/favicon.ico",
            "/**/*.json",
            "/**/*.yaml",
            "/**/*.yml",
            "/**/*.xml",
            "/**/*.properties",
            "/**/*.woff2",
            "/**/*.woff",
            "/**/*.ttf",
            "/**/*.ttc",
            "/**/*.ico",
            "/**/*.bmp",
            "/**/*.png",
            "/**/*.gif",
            "/**/*.svg",
            "/**/*.jpg",
            "/**/*.jpeg",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js").permitAll()
        .antMatchers("/auth/**").permitAll()
        .antMatchers("/search/**").permitAll()
        .anyRequest().authenticated();

    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}