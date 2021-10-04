package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {
    @NonNull
    private String username;
    @NonNull
    private String password;

    private AccountRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AccountRequestDto of(String username, String password) {
        return new AccountRequestDto(username, password);
    }
}
