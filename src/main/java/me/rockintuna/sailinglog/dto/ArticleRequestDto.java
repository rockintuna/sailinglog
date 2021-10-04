package me.rockintuna.sailinglog.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ArticleRequestDto {
    @NotNull
    private final String title;
    @NotNull
    private final String writer;
    @NotNull
    private final String content;

    private ArticleRequestDto(String title, String writer, String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
    }
    
    public static ArticleRequestDto of(String title, String writer, String content) {
        return new ArticleRequestDto(title, writer, content);
    }
}
