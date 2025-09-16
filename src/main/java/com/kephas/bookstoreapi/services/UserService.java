package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.enums.UserRole;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.kephas.bookstoreapi.constants.UserMessages.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    public User update(User user, UserDtoRequest userDtoRequest) {
        user.setEmail(userDtoRequest.email());
        user.setName(userDtoRequest.name());
        userRepository.save(user);

        return user;
    }

    public void changePassword(User user, UserDtoRequest userDtoRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        userDtoRequest.password()
                )
        );

        user.setPassword(passwordEncoder.encode(userDtoRequest.newPassword()));
        userRepository.save(user);
    }

    public String makeAdmin(UUID id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (user.getRole() == UserRole.ADMIN){
            return String.format(ALREADY_ADMIN, user.getName());
        }

        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        return String.format(NOW_ADMIN, user.getName());
    }

    public String unMakeAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if (user.getRole() == UserRole.USER){
            return String.format(ALREADY_NON_ADMIN,user.getName());
        }

        user.setRole(UserRole.USER);
        userRepository.save(user);
        return String.format(NO_LONGER_ADMIN, user.getName());
    }
}
