package me.rockintuna.sailinglog.config.validator;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.PasswordNeverContainsUsernameException;
import me.rockintuna.sailinglog.config.exception.PasswordNotEqualsWithCheckException;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.account.AccountRequestDto;
import me.rockintuna.sailinglog.account.AccountRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountRequestDtoValidator {
    private final AccountRepository accountRepository;

    public void validate(AccountRequestDto accountRequestDto) {
        if (!accountRequestDto.getPassword().equals(accountRequestDto.getPasswordCheck())) {
            throw new PasswordNotEqualsWithCheckException("패스워드가 일치하지 않습니다.");
        }
        if (accountRequestDto.getPassword().contains(accountRequestDto.getUsername())) {
            throw new PasswordNeverContainsUsernameException("패스워드는 닉네임을 포함할 수 없습니다.");
        }
        if ( accountRepository.findByUsername(accountRequestDto.getUsername()).isPresent() ) {
            throw new UsernameExistException("중복된 닉네임입니다.");
        }
    }
}
