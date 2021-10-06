package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import me.rockintuna.sailinglog.model.Article;

import javax.validation.constraints.NotBlank;


@Getter
public class CommentRequestDto {

    @NotBlank
    private Article article;
    @NotBlank
    private String content;

    private CommentRequestDto(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public CommentRequestDto of(Article article, String content) {
        return new CommentRequestDto(article, content);
    }
}
