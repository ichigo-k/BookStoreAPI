package com.kephas.bookstoreapi.dtos;

import com.kephas.bookstoreapi.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserDtoRequest(
        @NotBlank(message = "Name cannot be blank", groups = {OnCreate.class,OnUpdate.class })
        @NotNull(message = "Name cannot be null", groups = {OnCreate.class,OnUpdate.class })
        String name,

        @Email(message = "Please provide valid email" , groups = {OnCreate.class,OnUpdate.class ,OnLogin.class})
        @NotNull(message = "Email cannot be null" , groups = {OnCreate.class,OnUpdate.class ,OnLogin.class})
        String email,

        @NotBlank(message = "Password cannot be blank" , groups = {OnCreate.class, OnLogin.class})
        @NotNull(message = "Password cannot be empty" , groups = {OnCreate.class, OnLogin.class})
        @Size(min=8, message = "Password must be at least 8 characters long", groups = {OnCreate.class})
        String password,

        @NotBlank(message = "New password cannot be blank" , groups = {OnChangePassword.class})
        @NotNull(message = "New password cannot be empty" ,groups = {OnChangePassword.class})
        @Size(min=8, message = "New password must be at least 8 characters long", groups = {OnChangePassword.class})
        String newPassword,

        UserRole role

) {
        public interface OnCreate {}
        public interface OnUpdate {}
        public interface OnLogin {}
        public interface OnChangePassword{}
}
