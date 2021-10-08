package me.rockintuna.sailinglog.security;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.account.Account;
import me.rockintuna.sailinglog.account.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("유저가 존재하지 않습니다.")
        );
        return UserDetailsImpl.of(account);
    }

    public Account getAccountFrom(UserDetails userDetails) {
        if ( userDetails instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) userDetails).getAccount();
        } else {
            throw new RuntimeException("계정 정보가 존재하지 않습니다.");
        }
    }
}
