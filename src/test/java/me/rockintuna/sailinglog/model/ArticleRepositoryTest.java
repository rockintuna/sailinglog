package me.rockintuna.sailinglog.model;

import me.rockintuna.sailinglog.config.JpaAuditingConfiguration;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.repository.ArticleRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfiguration.class)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    List<Article> mockArticleList = new ArrayList<>();

    @BeforeEach
    private void beforeEach() throws InterruptedException {
        //given
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 1", "tester", "test content 1")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 2", "tester", "test content 2")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 3", "tester", "test content 3")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 4", "tester", "<script>alert('XSS');</script>")));

        for (Article article : mockArticleList) {
            articleRepository.save(article);
            sleep(10);
        }
    }


    @Test
    @DisplayName("최근 생성 순으로 게시글 목록 조회")
    public void findAllByOrderByCreatedAtDesc() {

        //when
        List<Article> articleList = articleRepository.findAllByOrderByCreatedAtDesc();

        //then
        SoftAssertions softly = new SoftAssertions();
        for (int i = 0; i < articleList.size()-1; i++) {
            softly.assertThat(articleList.get(i).getCreatedAt().isAfter(articleList.get(i+1).getCreatedAt()))
                    .isTrue();
        }
        softly.assertAll();
    }

    @Test
    @DisplayName("게시글 저장")
    public void save() {
        //given
        Article article = Article.from(
                new ArticleRequestDto("new title", "tester", "new content"));

        //when
        Article saved = articleRepository.save(article);

        //then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(saved.getId()).isNotNull();
        softly.assertThat(saved.getTitle()).isEqualTo(article.getTitle());
        softly.assertThat(saved.getWriter()).isEqualTo(article.getWriter());
        softly.assertThat(saved.getContent()).isEqualTo(article.getContent());
        softly.assertThat(saved.getCreatedAt()).isNotNull();
        softly.assertThat(saved.getModifiedAt()).isNotNull();
        softly.assertAll();
    }
}