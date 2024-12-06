package service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

        // enable the username & password utilization through a UI form
        // .formLogin(Customizer.withDefaults())

        // to allow authentication from postman and return access to REST API
        .httpBasic(Customizer.withDefaults())

        // making HTTP stateless and allows for new session ID in each access
        // so pass in valid username & password in the Authorization header in Postman
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Define role-based access rules
        .authorizeHttpRequests(
            auth ->
                auth
                    // Allow unauthenticated access to the createUser POST route
                    .requestMatchers(HttpMethod.POST, "/api/users/createUser")
                    .permitAll()

                    // Restrict DELETE routes to ADMIN only
                    .requestMatchers(HttpMethod.DELETE, "/**")
                    .hasRole("ADMIN")

                    // Allow users and admins to access everything else
                    .anyRequest()
                    .hasAnyRole("USER", "ADMIN"))
        .cors(Customizer.withDefaults())
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

  /** CORS configuration to allow requests from specific origins. */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**") // Apply CORS to all endpoints
            .allowedOrigins("http://localhost:3000") // Replace with your frontend URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(true) // Allow credentials (cookies or authorization headers)
            .maxAge(3600); // Cache preflight responses for 1 hour (3600 seconds)
      }
    };
  }
}
