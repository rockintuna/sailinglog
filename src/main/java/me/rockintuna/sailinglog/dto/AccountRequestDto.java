package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AccountRequestDto {
    @NotNull
    @Length(min=3)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String username;
    @NotNull
    @Length(min=4)
    private String password;
    @NotNull
    private String passwordCheck;

    private AccountRequestDto(String username, String password, String passwordCheck) {
        this.username = username;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }

    public static AccountRequestDto of(String username, String password, String passwordCheck) {
        return new AccountRequestDto(username, password, passwordCheck);
    }
}
