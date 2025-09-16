package com.kephas.bookstoreapi.dtos;

import static com.kephas.bookstoreapi.constants.ValidationMessages.*;
import com.kephas.bookstoreapi.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserDtoRequest(
        @NotBlank(message = USER_NAME_REQUIRED, groups = {OnCreate.class,OnUpdate.class })
        @NotNull(message = USER_NAME_REQUIRED, groups = {OnCreate.class,OnUpdate.class })
        String name,

        @Email(message = EMAIL_REQUIRED , groups = {OnCreate.class,OnUpdate.class ,OnLogin.class})
        @NotNull(message = EMAIL_INVALID , groups = {OnCreate.class,OnUpdate.class ,OnLogin.class})
        String email,

        @NotBlank(message = PASSWORD_REQUIRED , groups = {OnCreate.class, OnLogin.class})
        @NotNull(message = PASSWORD_REQUIRED , groups = {OnCreate.class, OnLogin.class})
        @Size(min=8, message = PASSWORD_MIN_LENGTH, groups = {OnCreate.class})
        String password,

        @NotBlank(message = NEW_PASSWORD_REQUIRED , groups = {OnChangePassword.class})
        @NotNull(message =NEW_PASSWORD_REQUIRED ,groups = {OnChangePassword.class})
        @Size(min=8, message = NEW_PASSWORD_MIN_LENGTH, groups = {OnChangePassword.class})
        String newPassword,

        UserRole role

) {
        public interface OnCreate {}
        public interface OnUpdate {}
        public interface OnLogin {}
        public interface OnChangePassword{}
}
