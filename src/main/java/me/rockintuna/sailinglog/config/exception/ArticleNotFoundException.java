package me.rockintuna.sailinglog.config.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException() {
        super("게시글을 찾을 수 없습니다.");
    }
}
