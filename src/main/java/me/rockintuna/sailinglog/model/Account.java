package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.dto.KakaoAccountDto;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Account extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(unique = true)
    private Long kakaoId;

    private Account(AccountRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.kakaoId = null;
    }

    private Account(KakaoAccountDto kakaoAccountDto) {
        this.username = kakaoAccountDto.getNickname();
        this.password = kakaoAccountDto.getPassword();
        this.kakaoId = kakaoAccountDto.getId();
    }

    public static Account from(AccountRequestDto requestDto) {
        return new Account(requestDto);
    }

    public static Account from(KakaoAccountDto kakaoAccountDto) {
        return new Account(kakaoAccountDto);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
