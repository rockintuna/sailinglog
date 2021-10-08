package me.rockintuna.sailinglog.article;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ArticleRequestDto {
    @NotBlank
    private final String title;
    @NotBlank
    private final String account;
    @NotBlank
    private final String content;

    private ArticleRequestDto(String title, String account, String content) {
        this.title = title;
        this.account = account;
        this.content = content;
    }
    
    public static ArticleRequestDto of(String title, String account, String content) {
        return new ArticleRequestDto(title, account, content);
    }
}
