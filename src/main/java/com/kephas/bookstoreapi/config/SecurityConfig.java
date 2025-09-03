package com.kephas.bookstoreapi.config;

import com.kephas.bookstoreapi.exceptions.InvalidLoginCredentialsException;
import com.kephas.bookstoreapi.exceptions.JwtAuthEntryPoint;
import com.kephas.bookstoreapi.exceptions.UnauthorizedHandler;
import com.kephas.bookstoreapi.repositories.UserRepository;
import com.kephas.bookstoreapi.utils.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/authors/**").hasAnyRole("USER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/authors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/authors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/authors/**").hasRole("ADMIN")


                        .anyRequest().permitAll()


        )
//                .exceptionHandling(ex ->
//                        ex.accessDeniedHandler(new UnauthorizedHandler())
//                                .authenticationEntryPoint(new JwtAuthEntryPoint())
//                )
                .csrf(csrf  -> csrf.disable())
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder (){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new InvalidLoginCredentialsException("Invalid login credentials"));
    }


}
