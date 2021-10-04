package me.rockintuna.sailinglog.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.PermissionDeniedException;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public List<Article> getArticlesOrderByCreatedAtDesc() {
        return articleService.getArticlesOrderByCreatedAtDesc();
    }

    @GetMapping("/articles/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping("/articles")
    public Article createArticle(@RequestBody @Valid ArticleRequestDto requestDto) {
        return articleService.createArticle(requestDto);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Void> updateArticle(
            @AuthenticationPrincipal UserDetails userDetails
            , @PathVariable Long id
            , @RequestBody @Valid ArticleRequestDto requestDto) {
        if (!userDetails.getUsername().equals(requestDto.getWriter())) {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
        articleService.updateArticle(id, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticleById(
            @AuthenticationPrincipal UserDetails userDetails
            ,@PathVariable Long id) {
        String username = userDetails.getUsername();
        articleService.deleteArticle(id, username);
        return ResponseEntity.ok().build();
    }
}
