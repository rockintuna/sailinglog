package me.rockintuna.sailinglog.service;

import me.rockintuna.sailinglog.config.exception.ArticleNotFoundException;
import me.rockintuna.sailinglog.config.exception.PermissionDeniedException;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.repository.ArticleRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
                ArticleRequestDto.of("test title 1", "tester", "test content 1")));
        mockArticleList.add(Article.from(
                ArticleRequestDto.of("test title 2", "tester", "test content 2")));
        mockArticleList.add(Article.from(
                ArticleRequestDto.of("test title 3", "tester", "test content 3")));
        mockArticleList.add(Article.from(
                ArticleRequestDto.of("test title 4", "tester", "<script>alert('XSS');</script>")));
    }


    @Nested
    @DisplayName("게시글 조회")
    class Read {
        @Nested
        @DisplayName("게시글 조회 성공")
        class ReadSuccess {
            @Test
            @DisplayName("게시글 목록 조회")
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
            @DisplayName("게시글 ID로 조회")
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
        }
        @Nested
        @DisplayName("게시글 조회 실패")
        class ReadFail {
            @Test
            @DisplayName("존재하지 않는 게시글 ID로 조회")
            void getArticleByInvalidId() {
                given(articleRepository.findById(99L))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> articleService.getArticleById(99L))
                        .isInstanceOf(ArticleNotFoundException.class);
                verify(articleRepository).findById(99L);
            }
        }
    }

    @Nested
    @DisplayName("게시글 생성")
    class Create {
        @Nested
        @DisplayName("게시글 생성 성공")
        class CreateSuccess {
            @Test
            @DisplayName("게시글 생성")
            void createArticle() {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
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
        }
        @Nested
        @DisplayName("게시글 생성 실패")
        class CreateFail {

        }
    }
    @Nested
    @DisplayName("게시글 수정")
    class Update {
        @Nested
        @DisplayName("게시글 수정 성공")
        class UpdateSuccess {
            @Test
            @DisplayName("게시글 ID로 게시글 수정")
            void updateArticle() {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("updated title 1", "tester", "updated content 1");

                given(articleRepository.findById(3L))
                        .willReturn(Optional.of(mockArticleList.get(0)));

                articleService.updateArticle(3L, requestDto);
                verify(articleRepository).save(any(Article.class));
            }
        }
        @Nested
        @DisplayName("게시글 수정 실패")
        class UpdateFail {
            @Test
            @DisplayName("존재하지 않는 게시글 ID로 수정")
            void updateArticleByInvalidId() {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("updated title 1", "tester", "updated content 1");
                given(articleRepository.findById(99L))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> articleService.updateArticle(99L, requestDto))
                        .isInstanceOf(ArticleNotFoundException.class);
                verify(articleRepository).findById(99L);
            }
        }
    }
    @Nested
    @DisplayName("게시글 삭제")
    class Delete {
        @Nested
        @DisplayName("게시글 삭제 성공")
        class DeleteSuccess {
            @Test
            @DisplayName("게시글 ID로 게시글 삭제")
            void deleteArticleById() {
                Article article = Article.from(
                        ArticleRequestDto.of("title", "jilee", "content")
                );

                given(articleRepository.findById(3L)).willReturn(Optional.of(article));

                articleService.deleteArticle(3L, "jilee");
                verify(articleRepository).deleteById(3L);
            }
        }
        @Nested
        @DisplayName("게시글 삭제 실패")
        class DeleteFail {
            @Test
            @DisplayName("존재하지 않는 게시글 ID로 삭제")
            void deleteArticleByInvalidId() {
                Article article = Article.from(
                        ArticleRequestDto.of("title", "tester", "content")
                );

                given(articleRepository.findById(99L)).willReturn(Optional.of(article));

                assertThrows(PermissionDeniedException.class,
                        () -> articleService.deleteArticle(99L, "jilee"));
                verify(articleRepository, never()).deleteById(any());
            }
        }
    }
}