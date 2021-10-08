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

        if (article.isWritedBy(username)) {
            article.updateBy(requestDto);
            articleRepository.save(article);
        } else {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
    }

    public void deleteArticle(Long id, String username) {
        Article article = getArticleById(id);

        if (article.isWritedBy(username)) {
            articleRepository.deleteById(id);
        } else {
            throw new PermissionDeniedException("권한이 없습니다.");
        }
    }
}
