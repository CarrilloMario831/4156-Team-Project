package service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.RequestContextFilter;

/** Defining our own configurations for security. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired private UserDetailsService userDetailsService;

  /** Defining our own custom security filter chain. */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        // disable CSRF
        .csrf(customizer -> customizer.disable())

        // enable the login form for authentication
        .authorizeHttpRequests(request -> request.anyRequest().authenticated())

        // enable the username & password utilization
        // .formLogin(Customizer.withDefaults())

        // to allow authentication from postman and return access to REST API
        .httpBasic(Customizer.withDefaults())

        // making HTTP stateless and allows for new session ID
        .sessionManagement (session -> session.sessionCreationPolicy(SessionCreationPolicy
         .STATELESS))
        .build();
  }

  /** Define how the user's login will function and connect to DB. */
  @Bean
  public AuthenticationProvider authenticationProvider() {

    // Dao is used for DB auth
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

    // no password encoder currently
    provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

    provider.setUserDetailsService(userDetailsService);

    // define our own userdetailsservice
    return provider;
  }
  
//  // sample security testing using hardcoded values
//  @Bean
//  public UserDetailsService userDetailsService() {
//    UserDetails user1 = User
//        .withDefaultPasswordEncoder()
//        .username("diego")
//        .password("diego123")
//        .roles("USER")
//        .build();
//
//    UserDetails user2 = User
//        .withDefaultPasswordEncoder()
//        .username("mario")
//        .password("mario123")
//        .roles("ADMIN")
//        .build();
//
//    return new InMemoryUserDetailsManager(user1, user2);
//  }
}
