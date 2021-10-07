package me.rockintuna.sailinglog.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.CommentNotFoundException;
import me.rockintuna.sailinglog.config.exception.PermissionDeniedException;
import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.model.Comment;
import me.rockintuna.sailinglog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleService articleService;

    public List<Comment> getCommentsByArticleIdOrderByCreatedAtDesc(Long articleId) {
        return commentRepository.findALlByArticleIdOrderByCreatedAtDesc(articleId);
    }

    public Comment createCommentOnArticle(Long articleId, Account account, CommentRequestDto requestDto) {
        Article article = articleService.getArticleById(articleId);
        Comment comment = Comment.of(account, article, requestDto);
        return commentRepository.save(comment);
    }

    public void updateComment(Long commentId, CommentRequestDto requestDto, String username) {
        Comment comment = getCommentById(commentId);
        if ( comment.isWriter(username) ) {
            comment.updateContent(requestDto.getContent());
            commentRepository.save(comment);
        } else {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
    }

    public void deleteCommentById(Long commentId, String username) {
        Comment comment = getCommentById(commentId);
        if ( comment.isWriter(username) ) {
            commentRepository.delete(comment);
        } else {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("댓글을 찾을 수 없습니다.")
        );
    }
}
