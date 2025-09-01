package com.kephas.bookstoreapi.dtos;

import com.kephas.bookstoreapi.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserDtoRequest(
        @NotBlank(message = "Name cannot be blank", groups = {OnCreate.class,OnUpdate.class })
        String name,

        @Email(message = "Please provide valid email" , groups = {OnCreate.class,OnUpdate.class })
        String email,

        @NotBlank(message = "Password cannot be blank" , groups = {OnCreate.class})
        @Size(min=8, message = "Password must be at least 8 characters long", groups = {OnCreate.class})
        String password,
        UserRole role

) {
        public interface OnCreate {}
        public interface OnUpdate {}
}
