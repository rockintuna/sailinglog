package me.rockintuna.sailinglog.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerAccount(AccountRequestDto requestDto) {
        if ( accountRepository.findByUsername(requestDto.getUsername()).isPresent() ) {
            throw new UsernameExistException("중복된 닉네임입니다.");
        }
        String password = requestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        requestDto.setPassword(encodedPassword);
        accountRepository.save(Account.from(requestDto));
    }
}
