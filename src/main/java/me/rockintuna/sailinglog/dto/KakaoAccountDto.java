package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KakaoAccountDto {

    @NonNull
    private Long id;
    @NonNull
    private String nickname;
    @NonNull
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
