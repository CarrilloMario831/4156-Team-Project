package service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.RequestContextFilter;

/** Defining our own configurations for security.*/
@Configuration
@EnableWebSecurity
public class SecurityConfig {
  
  /** Defining our own custom security filter chain.*/
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 RequestContextFilter requestContextFilter)
      throws Exception {
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
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))
        .build();
  }
}
