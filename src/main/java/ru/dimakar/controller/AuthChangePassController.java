package ru.dimakar.controller;

import ru.dimakar.dto.ChangePassRequest;
import ru.dimakar.dto.ChangePassResponse;
import ru.dimakar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class AuthChangePassController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/changepass")
    public ChangePassResponse changePass(@Valid @RequestBody ChangePassRequest changePassRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return userService.changePassword(changePassRequest, userDetails);
    }
}
