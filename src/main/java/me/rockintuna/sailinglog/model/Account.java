package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.sailinglog.dto.AccountRequestDto;

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

    private Account(AccountRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
    }

    public static Account from(AccountRequestDto requestDto) {
        return new Account(requestDto);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
