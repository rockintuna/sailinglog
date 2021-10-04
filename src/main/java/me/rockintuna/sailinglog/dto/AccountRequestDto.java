package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AccountRequestDto {
    @NotNull
    @Length(min=3,message = "닉네임은 최소 3자 이상입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "닉네임은 알파벡 대소문자 또는 숫자로만 이루어져있어야 합니다.")
    private String username;
    @NotNull
    @Length(min=4,message = "패스워드는 최소 4자 이상입니다.")
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
