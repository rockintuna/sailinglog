package me.rockintuna.sailinglog.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestBody AccountRequestDto requestDto) {
        accountService.registerAccount(requestDto);
        return "redirect:login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
