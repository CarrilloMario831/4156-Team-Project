package service.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import service.util.UserRoles;

/**
 * The type User principal tests.
 */
@SpringBootTest
public class UserPrincipalTests {

  /**
   * Test constructor and user details.
   */
  @Test
  public void testConstructorAndUserDetails() {
    User user =
        User.builder()
            .userId(UUID.randomUUID())
            .username("testUser")
            .password("password123")
            .role(UserRoles.ADMIN)
            .lastAccess(LocalDateTime.now())
            .build();

    UserPrincipal userPrincipal = new UserPrincipal(user);

    assertThat(userPrincipal.getUsername()).isEqualTo("testUser");
    assertThat(userPrincipal.getPassword()).isEqualTo("password123");
    assertThat(userPrincipal.isAccountNonExpired()).isTrue();
    assertThat(userPrincipal.isAccountNonLocked()).isTrue();
    assertThat(userPrincipal.isCredentialsNonExpired()).isTrue();
    assertThat(userPrincipal.isEnabled()).isTrue();
  }

  /**
   * Test get authorities with role.
   */
  @Test
  public void testGetAuthoritiesWithRole() {
    User user =
        User.builder()
            .userId(UUID.randomUUID())
            .username("testUser")
            .password("password123")
            .role(UserRoles.ADMIN)
            .lastAccess(LocalDateTime.now())
            .build();

    UserPrincipal userPrincipal = new UserPrincipal(user);
    Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

    assertThat(authorities).hasSize(1);
    assertThat(authorities.iterator().next())
        .isInstanceOf(SimpleGrantedAuthority.class)
        .extracting(GrantedAuthority::getAuthority)
        .isEqualTo("ROLE_ADMIN");
  }

  /**
   * Test edge cases.
   */
  @Test
  public void testEdgeCases() {
    User user =
        User.builder()
            .userId(UUID.randomUUID())
            .username("")
            .password("")
            .role(UserRoles.USER)
            .lastAccess(LocalDateTime.now())
            .build();

    UserPrincipal userPrincipal = new UserPrincipal(user);

    assertThat(userPrincipal.getUsername()).isEmpty();
    assertThat(userPrincipal.getPassword()).isEmpty();
    assertThat(userPrincipal.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_USER");
  }
}
