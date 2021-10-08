package me.rockintuna.sailinglog.article;

import lombok.RequiredArgsConstructor;
import me.rockintuna.sailinglog.config.exception.ArticleNotFoundException;
import me.rockintuna.sailinglog.config.exception.PermissionDeniedException;
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
        String username = requestDto.getAccount();
        checkPermission(article, username);
        article.updateBy(requestDto);
        articleRepository.save(article);
    }

    public void deleteArticle(Long id, String username) {
        Article article = getArticleById(id);
        checkPermission(article, username);
        articleRepository.deleteById(id);
    }

    private void checkPermission(Article article, String username) {
        if (!isOwner(article, username)) {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
    }

    private boolean isOwner(Article article, String username) {
        return username.equals(article.getAccount());
    }
}
