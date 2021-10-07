package me.rockintuna.sailinglog.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.model.Comment;
import me.rockintuna.sailinglog.service.CommentService;
import me.rockintuna.sailinglog.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/articles/{articleId}/comments")
    public List<Comment> getCommentsByArticleIdOrderByCreatedAtDesc(@PathVariable("articleId") Long articleId) {
        return commentService.getCommentsByArticleIdOrderByCreatedAtDesc(articleId);
    }

    @PostMapping("/articles/{articleId}/comments")
    public Comment createCommentOnArticle(@PathVariable("articleId") Long articleId,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody @Valid CommentRequestDto requestDto) {
        Account account = userDetailsService.getAccountFrom(userDetails);
        return commentService.createCommentOnArticle(articleId, account, requestDto);
    }

    @PutMapping("/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid CommentRequestDto requestDto) {
        String username = userDetails.getUsername();
        commentService.updateComment(commentId, requestDto, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentById(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        commentService.deleteCommentById(commentId, username);
        return ResponseEntity.ok().build();

    }
}
