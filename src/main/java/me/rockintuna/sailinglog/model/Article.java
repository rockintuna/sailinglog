package me.rockintuna.sailinglog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Article extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Article(ArticleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.writer = requestDto.getWriter();
        this.content = requestDto.getContent();
    }

    public static Article from(ArticleRequestDto requestDto) {
        return new Article(requestDto);
    }

    public void updateBy(ArticleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.writer = requestDto.getWriter();
        this.content = requestDto.getContent();
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}