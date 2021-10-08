package me.rockintuna.sailinglog.global.service;

import me.rockintuna.sailinglog.account.AccountService;
import me.rockintuna.sailinglog.config.exception.PasswordNeverContainsUsernameException;
import me.rockintuna.sailinglog.config.exception.PasswordNotEqualsWithCheckException;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.config.validator.AccountRequestDtoValidator;
import me.rockintuna.sailinglog.account.AccountRequestDto;
import me.rockintuna.sailinglog.account.Account;
import me.rockintuna.sailinglog.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService articleService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountRequestDtoValidator accountRequestDtoValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("계정 서비스")
    class Register {
        @Nested
        @DisplayName("회원 가입 성공")
        class RegisterSuccess {
            @Test
            @DisplayName("정상 데이터 회원 가입 성공")
            void registerAccount() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "password");

                given(passwordEncoder.encode(accountRequestDto.getPassword()))
                        .willReturn("encodedPassword");

                articleService.registerAccount(accountRequestDto);

                verify(accountRepository).save(any(Account.class));
            }
        }
        @Nested
        @DisplayName("회원 가입 실패")
        class RegisterFail {
            @Test
            @DisplayName("Username을 포함하는 암호")
            void registerFailedPasswordContainsUsername() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "passworD");

                willThrow(PasswordNeverContainsUsernameException.class)
                        .given(accountRequestDtoValidator).validate(accountRequestDto);

                assertThrows(PasswordNeverContainsUsernameException.class,
                        () -> articleService.registerAccount(accountRequestDto));

                verify(accountRepository,never()).save(any(Account.class));
            }

            @Test
            @DisplayName("패스워드 확인 틀림")
            void registerFailedPasswordNotEqualsWithCheck() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "passworD");

                willThrow(PasswordNotEqualsWithCheckException.class)
                        .given(accountRequestDtoValidator).validate(accountRequestDto);

                assertThrows(PasswordNotEqualsWithCheckException.class,
                        () -> articleService.registerAccount(accountRequestDto));

                verify(accountRepository,never()).save(any(Account.class));
            }

            @Test
            @DisplayName("이미 사용중인 Username")
            void registerFailedUsernameExist() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "passworD");

                willThrow(UsernameExistException.class)
                        .given(accountRequestDtoValidator).validate(accountRequestDto);

                assertThrows(UsernameExistException.class,
                        () -> articleService.registerAccount(accountRequestDto));

                verify(accountRepository,never()).save(any(Account.class));
            }
        }

    }

}