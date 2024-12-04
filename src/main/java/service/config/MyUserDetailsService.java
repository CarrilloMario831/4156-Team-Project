package service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.handler.UsersTableSqlHelper;
import service.models.User;
import service.models.UserPrincipal;

/** Define how we should connect the user validation to our DB. */
@Service
public class MyUserDetailsService implements UserDetailsService {

  @Autowired private UsersTableSqlHelper usersTableSqlHelper;

  /** Describe how the user login will interact with DB. */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = usersTableSqlHelper.getUserWithUsername(username);

    if (user == null) {
      System.out.println("User Not Found");
      throw new UsernameNotFoundException("user not found");
    }

    System.out.println("User was found: " + user.getUsername());
    return new UserPrincipal(user);
  }
}
