package me.rockintuna.sailinglog.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ArticleRequestDto {
    @NonNull
    private final String title;
    @NonNull
    private final String writer;
    @NonNull
    private final String content;

    private ArticleRequestDto(@NonNull String title, @NonNull String writer, @NonNull String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
    }
    
    public static ArticleRequestDto of(String title, String writer, String content) {
        return new ArticleRequestDto(title, writer, content);
    }
}
