package me.rockintuna.sailinglog.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.domain.Article;
import me.rockintuna.sailinglog.domain.ArticleRequestDto;
import me.rockintuna.sailinglog.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Article createArticle(@RequestBody ArticleRequestDto requestDto) {
        return articleService.createArticle(requestDto);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Void> updateArticle(@PathVariable Long id
            , @RequestBody ArticleRequestDto requestDto) {
        articleService.updateArticle(id, requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        articleService.deleteArticleById(id);
        return ResponseEntity.ok().build();
    }
}
