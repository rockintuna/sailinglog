package me.rockintuna.sailinglog.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Comment;
import me.rockintuna.sailinglog.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/articles/{articleId}/comments")
    public List<Comment> getCommentsByArticleId(@PathVariable("articleId") Long articleId) {
        return commentService.getCommentsByArticleId(articleId);
    }

    @PostMapping("/articles/{articleId}/comments")
    public Comment createCommentOnArticle(@PathVariable("articleId") Long articleId) {
        return commentService.createCommentOnArticle(articleId);
    }

    @PutMapping("/articles/{articleId}/comments/{commentId}")
    public Comment updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid CommentRequestDto requestDto) {
        String username = userDetails.getUsername();
        return commentService.updateComment(commentId, requestDto, username);
    }

    @DeleteMapping("/articles/{articleId}/comments/{commentId}")
    public Comment deleteCommentById(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return commentService.deleteCommentById(commentId, username);
    }
}
