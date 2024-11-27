package service.models;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Implementing class needed for the MyDetailsService methods. */
public class UserPrincipal implements UserDetails {

  private User user;

  /** Allow the passing of the user object to just get the UserDetails object type. */
  public UserPrincipal(User user) {
    this.user = user;
  }

  /**
   * This stores the role of the user for the authorization processes to work properly for the
   * UserDetailsService class.
   *
   * @return Collection representing the authority of the user.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
  }

  /**
   * This stores the password grabbed from the DB.
   *
   * @return String representing the password of the user.
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * Grab the username of the user object passed into the constructor.
   *
   * @return String representing the username of the user.
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Just return true that the user's account haven't expired.
   *
   * @return true just return true for now.
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Just return true that the user's account is not locked.
   *
   * @return true just return true for now.
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Just return true that the user's credentials haven't expired.
   *
   * @return true just return true for now.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Just return true that the user is enabled.
   *
   * @return true just return true for now.
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
