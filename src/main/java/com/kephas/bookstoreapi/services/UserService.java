package com.kephas.bookstoreapi.services;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.entities.UserRole;
import com.kephas.bookstoreapi.exceptions.ResourceNotFoundException;
import com.kephas.bookstoreapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        if (user.getRole() == UserRole.ADMIN){
            return user.getName()+ " is already an admin";
        }

        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        return user.getName()+ " is now an admin";
    }

    public String unMakeAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        if (user.getRole() == UserRole.USER){
            return user.getName()+ " is already non admin";
        }

        user.setRole(UserRole.USER);
        userRepository.save(user);
        return user.getName()+ " is no longer an admin";
    }
}
