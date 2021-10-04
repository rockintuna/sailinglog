package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import me.rockintuna.sailinglog.service.Oauth2Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final Oauth2Service oauth2Service;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid AccountRequestDto requestDto) {
        accountService.registerAccount(requestDto);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/oauth/callback")
    public String oauth2Callback(@RequestParam String code) throws JsonProcessingException {
        oauth2Service.login(code);
        return "redirect:/";
    }

}
