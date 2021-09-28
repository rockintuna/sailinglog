package me.rockintuna.sailinglog.service;

import me.rockintuna.sailinglog.domain.Article;
import me.rockintuna.sailinglog.domain.ArticleRepository;
import me.rockintuna.sailinglog.domain.ArticleRequestDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    List<Article> mockArticleList = new ArrayList<>();

    @BeforeEach
    private void beforeEach() {
        //given
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 1", "tester", "test content 1")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 2", "tester", "test content 2")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 3", "tester", "test content 3")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 4", "tester", "<script>alert('XSS');</script>")));
    }

    @Test
    void getArticlesOrderByCreatedAtDesc() {
        // given
        given(articleRepository.findAllByOrderByCreatedAtDesc()).willReturn(mockArticleList);

        // when
        List<Article> articleList = articleService.getArticlesOrderByCreatedAtDesc();

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(articleList.size()).isEqualTo(mockArticleList.size());
        softly.assertThat(articleList.get(0).getTitle()).isEqualTo(mockArticleList.get(0).getTitle());
        softly.assertThat(articleList.get(0).getWriter()).isEqualTo(mockArticleList.get(0).getWriter());
        softly.assertThat(articleList.get(0).getContent()).isEqualTo(mockArticleList.get(0).getContent());
        softly.assertThat(articleList.get(3).getTitle()).isEqualTo(mockArticleList.get(3).getTitle());
        softly.assertThat(articleList.get(3).getWriter()).isEqualTo(mockArticleList.get(3).getWriter());
        softly.assertThat(articleList.get(3).getContent()).isEqualTo(mockArticleList.get(3).getContent());
        softly.assertAll();
        verify(articleRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getArticleById() {
        given(articleRepository.findById(1L))
                .willReturn(Optional.of(mockArticleList.get(0)));

        Article article = articleService.getArticleById(1L);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(article.getTitle()).isEqualTo(mockArticleList.get(0).getTitle());
        softly.assertThat(article.getWriter()).isEqualTo(mockArticleList.get(0).getWriter());
        softly.assertThat(article.getContent()).isEqualTo(mockArticleList.get(0).getContent());
        softly.assertAll();
        verify(articleRepository).findById(1L);
    }

    @Test
    void createArticle() {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        Article mockArticle = Article.from(requestDto);
        given(articleRepository.save(any(Article.class))).willReturn(mockArticle);

        Article savedArticle = articleService.createArticle(requestDto);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(savedArticle.getTitle()).isEqualTo(mockArticle.getTitle());
        softly.assertThat(savedArticle.getWriter()).isEqualTo(mockArticle.getWriter());
        softly.assertThat(savedArticle.getContent()).isEqualTo(mockArticle.getContent());
        softly.assertAll();
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void updateArticle() {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("updated title 1", "tester", "updated content 1");
        //todo

//        Long articleId = articleService.updateArticle(3L, requestDto);

//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(articleId).isEqualTo(3L);
//        softly.assertAll();

//        verify(articleService).getArticleById(3L);
    }

    @Test
    void deleteArticleById() {
        Long articleId = articleService.deleteArticleById(3L);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(articleId).isEqualTo(3L);
        softly.assertAll();
        verify(articleRepository).deleteById(3L);
    }
}