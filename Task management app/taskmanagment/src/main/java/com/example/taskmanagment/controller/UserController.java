package com.example.taskmanagment.controller;

import com.example.taskmanagment.model.DTOs.*;
import com.example.taskmanagment.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController extends AbstractController{
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public UserWithoutPassDTO register(@Valid @RequestBody final RegisterDTO registerData){
        return userService.register(registerData);
    }
    @PostMapping("/users/sign-in")
    public UserWithoutPassDTO login (@Valid @RequestBody final LoginDTO loginData, final HttpSession s){
        final UserWithoutPassDTO u= userService.login(loginData);
        s.setAttribute(LOGGED,true);
        s.setAttribute(LOGGED_ID,u.getId());
        return u;
    }
    @PostMapping("/users/sign-out")
    public ResponseEntity<String> logOut(final HttpSession s) {
        getLoggedId(s);
        s.invalidate();
        return ResponseEntity.ok("Sign out was successful.");
    }
    @PutMapping("/users")
    public UserWithoutPassDTO editUser(@Valid @RequestBody final EditUserDTO editData, final HttpSession s){
        final long userId=getLoggedId(s);
        return userService.editUser(userId, editData);
    }
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteAccount(final HttpSession s, final UserPassDTO passDTO) {
        final long loggedId=getLoggedId(s);
        userService.deleteAccount(loggedId, passDTO);
        s.invalidate();
        return ResponseEntity.ok("Delete was successful.");
    }

}
