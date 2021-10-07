package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import me.rockintuna.sailinglog.model.Article;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
public class CommentRequestDto {

    @NotNull
    private Article article;
    @NotBlank
    private String content;

    private CommentRequestDto(Article article, String content) {
        this.article = article;
        this.content = content;
    }

    public static CommentRequestDto of(Article article, String content) {
        return new CommentRequestDto(article, content);
    }
}
