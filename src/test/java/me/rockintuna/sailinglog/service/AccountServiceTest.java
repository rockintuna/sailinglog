package me.rockintuna.sailinglog.service;

import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService articleService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void registerAccount() {
        AccountRequestDto accountRequestDto =
                AccountRequestDto.of("jilee", "password");

        given(passwordEncoder.encode(accountRequestDto.getPassword()))
                .willReturn("encodedPassword");

        articleService.registerAccount(accountRequestDto);

        verify(accountRepository).findByUsername("jilee");
        verify(passwordEncoder).encode("password");
        verify(accountRepository).save(any(Account.class));
    }
}