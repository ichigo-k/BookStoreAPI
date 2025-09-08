package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.UserDtoRequest;
import com.kephas.bookstoreapi.services.AuthService;
import com.kephas.bookstoreapi.utils.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signUp( @Validated(UserDtoRequest.OnCreate.class) @RequestBody UserDtoRequest userDtoRequest){
        authService.signUp(userDtoRequest);
        ApiResponse<Object> response = ApiResponse.success(201,"Account registered successfully", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Validated(UserDtoRequest.OnLogin.class) @RequestBody UserDtoRequest userDtoRequest){
        Map<String, Object> data = authService.login(userDtoRequest);
        ApiResponse<Object> response = ApiResponse.success(200,"Login successful", data.get("token"));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", data.get("cookie").toString())
                .body(response);
    }

}
