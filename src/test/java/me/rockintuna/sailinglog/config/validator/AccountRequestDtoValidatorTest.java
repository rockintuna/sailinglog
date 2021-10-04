package me.rockintuna.sailinglog.config.validator;

import me.rockintuna.sailinglog.config.exception.PasswordNeverContainsUsernameException;
import me.rockintuna.sailinglog.config.exception.PasswordNotEqualsWithCheckException;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountRequestDtoValidatorTest {

    @InjectMocks
    private AccountRequestDtoValidator accountRequestDtoValidator;

    @Mock
    private AccountRepository accountRepository;

    @Nested
    @DisplayName("AccountRequestDto 검증")
    class Register {
        @Nested
        @DisplayName("검증 성공")
        class RegisterSuccess {
            @Test
            @DisplayName("정상 데이터 회원 가입 성공")
            void validateSuccess() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "password");

                accountRequestDtoValidator.validate(accountRequestDto);
            }
        }
        @Nested
        @DisplayName("검증 실패")
        class RegisterFail {
            @Test
            @DisplayName("패스워드 확인 틀림")
            void validateFailedPasswordContainsUsername() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "passworD");

                assertThrows(PasswordNotEqualsWithCheckException.class,
                        () -> accountRequestDtoValidator.validate(accountRequestDto)
                );
            }

            @Test
            @DisplayName("Username을 포함하는 암호")
            void validateFailedPasswordNotEqualsWithCheck() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "1jilee123", "1jilee123");

                assertThrows(PasswordNeverContainsUsernameException.class,
                        () -> accountRequestDtoValidator.validate(accountRequestDto)
                );
            }

            @Test
            @DisplayName("이미 사용중인 Username")
            void validateFailedUsernameExist() {
                AccountRequestDto accountRequestDto =
                        AccountRequestDto.of("jilee", "password", "password");
                given(accountRepository.findByUsername("jilee"))
                        .willReturn(Optional.of(Account.from(accountRequestDto)));

                assertThrows(UsernameExistException.class,
                        () -> accountRequestDtoValidator.validate(accountRequestDto)
                );
            }
        }

    }


}