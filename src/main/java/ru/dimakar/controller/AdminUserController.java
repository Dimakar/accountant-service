package ru.dimakar.controller;

import ru.dimakar.dto.*;
import ru.dimakar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class AdminUserController {
    @Autowired
    private UserService userService;

    @GetMapping("/api/admin/user")
    public List<UserDto> getUser() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/api/admin/user")
    public DeleteUserResponse deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @DeleteMapping("/api/admin/user/{email}")
    public DeleteUserResponse deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }


    @PutMapping("/api/admin/user/role")
    public UserDto changeRole(@RequestBody ChangeRolesDto changeRolesDto) {
        return userService.changeRole(changeRolesDto);
    }

    @PutMapping("/api/admin/user/access")
    public ChangePassResponse changeAccess(@RequestBody ChangeAccessRequest changeAccessRequest) {
        return userService.changeAccess(changeAccessRequest);
    }
}
