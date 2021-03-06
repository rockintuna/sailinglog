package me.rockintuna.sailinglog.account;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.validator.AccountRequestDtoValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRequestDtoValidator accountRequestDtoValidator;

    public void registerAccount(AccountRequestDto requestDto) {
        accountRequestDtoValidator.validate(requestDto);
        AccountRequestDto passwordEncodedRequestDto = encodePasswordOf(requestDto);
        accountRepository.save(Account.from(passwordEncodedRequestDto));
    }

    private AccountRequestDto encodePasswordOf(AccountRequestDto requestDto) {
        String password = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        requestDto.setPassword(encodedPassword);
        return requestDto;
    }
}
