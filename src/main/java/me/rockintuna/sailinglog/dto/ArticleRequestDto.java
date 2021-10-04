package me.rockintuna.sailinglog.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ArticleRequestDto {
    @NotBlank
    private final String title;
    @NotBlank
    private final String writer;
    @NotBlank
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
