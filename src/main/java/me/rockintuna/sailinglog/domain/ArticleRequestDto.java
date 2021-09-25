package me.rockintuna.sailinglog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleRequestDto {
    private final String title;
    private final String writer;
    private final String content;
}
