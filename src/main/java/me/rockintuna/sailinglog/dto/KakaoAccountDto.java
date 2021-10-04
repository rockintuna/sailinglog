package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KakaoAccountDto {

    private Long id;
    private String nickname;
    private String password;

    private KakaoAccountDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.password = UUID.randomUUID().toString();
    }

    public static KakaoAccountDto of(Long id, String nickname) {
        return new KakaoAccountDto(id, nickname);
    }
}
