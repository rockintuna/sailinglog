package me.rockintuna.sailinglog.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.ArticleNotFoundException;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.repository.ArticleRepository;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getArticlesOrderByCreatedAtDesc() {
        return articleRepository.findAllByOrderByCreatedAtDesc();
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(
                ArticleNotFoundException::new
        );
    }

    public Article createArticle(ArticleRequestDto requestDto) {
        Article article = Article.from(requestDto);
        return articleRepository.save(article);
    }

    public void updateArticle(Long id, ArticleRequestDto requestDto) {
        Article article = getArticleById(id);
        article.updateBy(requestDto);
        articleRepository.save(article);
    }

    public void deleteArticleById(Long id) {
        try {
            articleRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ArticleNotFoundException();
        }

    }
}
