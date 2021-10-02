package me.rockintuna.sailinglog.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Account extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
