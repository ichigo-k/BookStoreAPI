package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.exceptions.UniqueConstraintViolationException;
import com.kephas.bookstoreapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(@Valid UserDtoRequest userDtoRequest) {
        if (userRepository.existsByEmail(userDtoRequest.email())){
            throw new UniqueConstraintViolationException("Email already taken");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userDtoRequest.password()));
        user.setName(userDtoRequest.name());
        user.setEmail(userDtoRequest.email());
        userRepository.save(user);
    }



    public Map<String, Object> login(UserDtoRequest userDtoRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDtoRequest.email(),
                        userDtoRequest.password()
                )
        );

        String token = jwtService.generateToken(authentication.getName());

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("lax")
                .maxAge(Duration.ofHours(1))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("cookie", cookie);

        return response;
    }

}
