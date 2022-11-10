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
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserServiceImpl userDetailsService;

  private final JwtAuthenticationEntryPoint jwtEntryPoint;

  @Autowired
  public WebSecurityConfig(
      UserServiceImpl userDetailsService, JwtAuthenticationEntryPoint jwtEntryPoint) {
    this.userDetailsService = userDetailsService;
    this.jwtEntryPoint = jwtEntryPoint;
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
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
        .anyRequest().authenticated();

    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}