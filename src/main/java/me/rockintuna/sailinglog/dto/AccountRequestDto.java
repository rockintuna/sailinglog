package me.rockintuna.sailinglog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AccountRequestDto {
    private String username;
    private String password;
}
