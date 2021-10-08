package me.rockintuna.sailinglog.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByKakaoId(Long kakaoId);
}
