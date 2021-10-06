package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    Account writer;
    @Column(nullable = false)
    Article article;
    @Column(nullable = false)
    String content;



}
