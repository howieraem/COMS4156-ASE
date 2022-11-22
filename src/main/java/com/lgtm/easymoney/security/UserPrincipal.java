package com.lgtm.easymoney.security;

import com.lgtm.easymoney.models.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** A wrapper of the User class to retrieve user details with a jwt. */
public class UserPrincipal implements UserDetails {
  private final User user;

  /** Construct user details by copying attributes of an existing user. */
  public UserPrincipal(final User user) {
    this.user = user;
  }

  public Long getId() {
    return user.getId();
  }

  public User get() {
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("USER"));
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UserPrincipal that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId());
  }
}
