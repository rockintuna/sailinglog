package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.sailinglog.dto.CommentRequestDto;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    Account account;

    @ManyToOne
    @JoinColumn(name = "ARTICLE_ID", nullable = false)
    Article article;

    @Column(nullable = false)
    String content;

    private Comment(Account account, CommentRequestDto requestDto) {
        this.account = account;
        this.article = requestDto.getArticle();
        this.content = requestDto.getContent();
    }

    public static Comment of(Account account, CommentRequestDto requestDto) {
        return new Comment(account, requestDto);
    }
}
