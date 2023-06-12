package com.example.taskmanagment.controller;

import com.example.taskmanagment.model.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AbstractController {
    protected static final String LOGGED = "LOGGED";
    protected static final String LOGGED_ID = "LOGGED_ID";

    protected long getLoggedId(HttpSession s){
        if(s.getAttribute(LOGGED_ID)==null){
            throw new UnauthorizedException("You have to login first.");
        }
        return (long) s.getAttribute(LOGGED_ID);
    }
}
