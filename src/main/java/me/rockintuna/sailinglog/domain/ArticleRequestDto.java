package me.rockintuna.sailinglog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ArticleRequestDto {
    @NonNull
    private final String title;
    @NonNull
    private final String writer;
    @NonNull
    private final String content;
}
