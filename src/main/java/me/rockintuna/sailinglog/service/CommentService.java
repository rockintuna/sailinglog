package me.rockintuna.sailinglog.service;

import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Comment;

import java.util.List;

public class CommentService {
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return null;
    }

    public Comment createCommentOnArticle(Long articleId) {
        return null;
    }

    public Comment updateComment(Long commentId, CommentRequestDto requestDto, String username) {
        return null;
    }

    public Comment deleteCommentById(Long commentId, String username) {
        return null;
    }
}
