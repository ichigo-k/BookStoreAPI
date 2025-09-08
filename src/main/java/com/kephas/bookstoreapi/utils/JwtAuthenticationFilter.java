package com.kephas.bookstoreapi.utils;

import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.exceptions.InvalidLoginCredentialsException;
import com.kephas.bookstoreapi.exceptions.JwtAuthEntryPoint;
import com.kephas.bookstoreapi.repositories.UserRepository;
import com.kephas.bookstoreapi.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtAuthEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new InvalidLoginCredentialsException("Invalid or expired token"));

                if (jwtService.validateToken(token, user.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            authenticationEntryPoint.commence(request, response,
                    new InvalidLoginCredentialsException("JWT token is expired"));

        } catch (MalformedJwtException ex) {
            authenticationEntryPoint.commence(request, response,
                    new InvalidLoginCredentialsException("Malformed JWT token"));

        } catch (SignatureException ex) {
            authenticationEntryPoint.commence(request, response,
                    new InvalidLoginCredentialsException("Invalid JWT signature"));

        } catch (Exception ex) {
            authenticationEntryPoint.commence(request, response,
                    new InvalidLoginCredentialsException("Authentication failed"));
        }
    }
}
