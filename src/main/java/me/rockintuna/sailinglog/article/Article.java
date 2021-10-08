package me.rockintuna.sailinglog.article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.sailinglog.article.comment.Comment;
import me.rockintuna.sailinglog.global.model.Timestamped;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Article extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> commentList = new ArrayList<>();

    private Article(ArticleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.account = requestDto.getAccount();
        this.content = requestDto.getContent();
    }

    public static Article from(ArticleRequestDto requestDto) {
        return new Article(requestDto);
    }

    public void updateBy(ArticleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.account = requestDto.getAccount();
        this.content = requestDto.getContent();
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", account='" + account + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}