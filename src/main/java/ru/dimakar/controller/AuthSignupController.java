package ru.dimakar.controller;

import ru.dimakar.dto.SignUpRequest;
import ru.dimakar.dto.UserDto;
import ru.dimakar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class AuthSignupController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/signup")
    public UserDto signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return userService.save(signUpRequest);
    }
}
