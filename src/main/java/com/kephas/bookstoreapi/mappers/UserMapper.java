package com.kephas.bookstoreapi.mappers;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.dtos.UserDtoResponse;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.entities.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {


    public User fromDto(UserDtoRequest userDtoRequest) {
        return new User(
                null,
                userDtoRequest.name(),
                userDtoRequest.email(),
                userDtoRequest.password(),
                userDtoRequest.role(),
                null
        );
    }


    public UserDtoResponse toDto(User user) {
        return new UserDtoResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
