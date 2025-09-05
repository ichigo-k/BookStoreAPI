package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.dtos.UserDtoResponse;
import com.kephas.bookstoreapi.entities.User;
import com.kephas.bookstoreapi.mappers.UserMapper;
import com.kephas.bookstoreapi.services.UserService;
import com.kephas.bookstoreapi.utils.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public  ApiResponse<Object> profile(@AuthenticationPrincipal User user){
        UserDtoResponse userDtoResponse = userMapper.toDto(user);
        return ApiResponse.success(null, userDtoResponse);
    }

    @PatchMapping("/")
    public  ApiResponse<Object> updateDetails(@AuthenticationPrincipal User user,  @Validated(UserDtoRequest.OnUpdate.class) @RequestBody UserDtoRequest userDtoRequest ){
        UserDtoResponse userDtoResponse = userMapper.toDto(userService.update(user, userDtoRequest));
        return ApiResponse.success("Details update successfully", userDtoResponse);
    }

    @PostMapping("/change-password")
    public  ApiResponse<Object> changePassword(@AuthenticationPrincipal User user,  @Validated(UserDtoRequest.OnChangePassword.class) @RequestBody UserDtoRequest userDtoRequest ){
        userService.changePassword(user, userDtoRequest);
        return ApiResponse.success("Password changed successfully", null);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/make-admin/{id}")
    public  ApiResponse<Object> makeAdmin(@PathVariable UUID id){
        String message = userService.makeAdmin(id );
        return ApiResponse.success(message, null);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unmake-admin/{id}")
    public  ApiResponse<Object> unMakeAdmin(@PathVariable UUID id){
        String message = userService.unMakeAdmin(id);
        return ApiResponse.success(message, null);
    }
}
