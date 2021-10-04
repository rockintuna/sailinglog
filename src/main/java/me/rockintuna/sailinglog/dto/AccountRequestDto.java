package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AccountRequestDto {
    @NotBlank
    @Length(min=3)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String username;
    @NotBlank
    @Length(min=4)
    private String password;
    @NotBlank
    private String passwordCheck;

    private AccountRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AccountRequestDto of(String username, String password) {
        return new AccountRequestDto(username, password);
    }
}
