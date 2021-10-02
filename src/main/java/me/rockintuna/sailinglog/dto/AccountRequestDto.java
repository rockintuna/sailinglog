package me.rockintuna.sailinglog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;

@AllArgsConstructor
@Getter
public class AccountRequestDto {
    private String username;
    private String password;
}
