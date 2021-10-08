package me.rockintuna.sailinglog.article.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @NotBlank
    private String content;

    private CommentRequestDto(String content) {
        this.content = content;
    }

    public static CommentRequestDto contentOf(String content) {
        return new CommentRequestDto(content);
    }
}
