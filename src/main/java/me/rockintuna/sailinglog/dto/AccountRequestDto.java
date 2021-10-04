package me.rockintuna.sailinglog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {
    private String username;
    private String password;

    private AccountRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AccountRequestDto of(String username, String password) {
        return new AccountRequestDto(username, password);
    }
}
