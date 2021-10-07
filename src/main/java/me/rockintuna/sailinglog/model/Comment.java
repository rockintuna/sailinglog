package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private Comment(Account account, Article article, CommentRequestDto requestDto) {
        this.account = account;
        this.article = article;
        this.content = requestDto.getContent();
    }

    public static Comment of(Account account, Article article, CommentRequestDto requestDto) {
        return new Comment(account, article, requestDto);
    }

    public boolean isWriter(String username) {
        return this.account.getUsername().equals(username);
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
