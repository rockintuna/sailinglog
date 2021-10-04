package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.service.AccountService;
import me.rockintuna.sailinglog.service.Oauth2Service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final Oauth2Service oauth2Service;

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if ( userDetails != null ) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid AccountRequestDto requestDto) {
        accountService.registerAccount(requestDto);
        return "redirect:/account/login";
    }

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if ( userDetails != null ) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "login";
    }

    @GetMapping("/oauth/callback")
    public String oauth2Callback(@RequestParam String code) throws JsonProcessingException {
        oauth2Service.login(code);
        return "redirect:/";
    }

}
