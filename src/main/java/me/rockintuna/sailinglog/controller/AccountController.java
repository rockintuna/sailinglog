package me.rockintuna.sailinglog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {


    @GetMapping("/register")
    public String registerPage() {
        return null;
    }

    @PostMapping("/register")
    public String register() {
        return null;
    }

    @GetMapping("/login")
    public String loginPage() {
        return null;
    }

    @PostMapping("/login")
    public String login() {
        return null;
    }

}
