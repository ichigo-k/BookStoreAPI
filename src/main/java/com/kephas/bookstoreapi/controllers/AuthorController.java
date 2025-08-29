package com.kephas.bookstoreapi.controllers;

import com.kephas.bookstoreapi.dtos.AuthorDto;
import com.kephas.bookstoreapi.entities.Author;
import com.kephas.bookstoreapi.utils.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController {

    @GetMapping("/")
    public ApiResponse<List<AuthorDto>> getAuthors(){

    }

}
