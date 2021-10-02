package me.rockintuna.sailinglog.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.UsernameExistException;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void registerAccount(AccountRequestDto requestDto) {
        if ( accountRepository.findByUsername(requestDto.getUsername()).isPresent() ) {
            throw new UsernameExistException("이미 존재하는 username 입니다.");
        }
        accountRepository.save(Account.from(requestDto));
    }
}
