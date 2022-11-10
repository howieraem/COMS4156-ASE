package com.lgtm.easymoney.security;

import com.lgtm.easymoney.models.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends User implements UserDetails {
  public CustomUserDetails(final User user) {
    setId(user.getId());
    setEmail(user.getEmail());
    setPassword(user.getPassword());
    setPhone(user.getPhone());
    setAddress(user.getAddress());
    setType(user.getType());
    setBalance(user.getBalance());
    setAccount(user.getAccount());
    setBizProfile(user.getBizProfile());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("USER"));
  }

  @Override
  public String getPassword() {
    return super.getPassword();
  }

  @Override
  public String getUsername() {
    return super.getEmail();
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
    if (obj == this) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    CustomUserDetails that = (CustomUserDetails) obj;
    return Objects.equals(getId(), that.getId());
  }
}
