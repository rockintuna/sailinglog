package me.rockintuna.sailinglog.global.service;

import me.rockintuna.sailinglog.account.AccountRequestDto;
import me.rockintuna.sailinglog.article.ArticleRequestDto;
import me.rockintuna.sailinglog.article.ArticleService;
import me.rockintuna.sailinglog.article.comment.CommentRequestDto;
import me.rockintuna.sailinglog.account.Account;
import me.rockintuna.sailinglog.article.Article;
import me.rockintuna.sailinglog.article.comment.Comment;
import me.rockintuna.sailinglog.article.comment.CommentRepository;
import me.rockintuna.sailinglog.article.comment.CommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleService articleService;

    private List<Comment> mockCommentList = new ArrayList<>();
    private Article mockArticle;
    private Account account;

    @BeforeEach
    private void beforeEach() {
        account = Account.from(AccountRequestDto.of("jilee", "password", "password"));
        mockArticle = Article.from(
                ArticleRequestDto.of("test title 1", "tester", "test content 1"));

        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments1")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments2")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments3")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments4")));
    }

    @Nested
    @DisplayName("?????? ??????")
    class Read {
        @Nested
        @DisplayName("?????? ?????? ??????")
        class ReadSuccess {
            @Test
            @DisplayName("?????? ?????? ??????")
            void getCommentsByArticleId() {
                given(commentRepository.findALlByArticleIdOrderByCreatedAtDesc(1L)).willReturn(mockCommentList);

                List<Comment> commentList = commentService.getCommentsByArticleIdOrderByCreatedAtDesc(1L);

                SoftAssertions softly = new SoftAssertions();
                softly.assertThat(commentList.size()).isEqualTo(mockCommentList.size());
                softly.assertThat(commentList.get(0).getAccount()).isEqualTo(mockCommentList.get(0).getAccount());
                softly.assertThat(commentList.get(0).getContent()).isEqualTo(mockCommentList.get(0).getContent());
                softly.assertThat(commentList.get(3).getAccount()).isEqualTo(mockCommentList.get(3).getAccount());
                softly.assertThat(commentList.get(3).getContent()).isEqualTo(mockCommentList.get(3).getContent());
                softly.assertAll();
                verify(commentRepository).findALlByArticleIdOrderByCreatedAtDesc(1L);
            }

        }
    }
    @Nested
    @DisplayName("?????? ??????")
    class Create {
        @Nested
        @DisplayName("?????? ?????? ??????")
        class CreateSuccess {
            @Test
            @DisplayName("?????? ??????")
            void createCommentOnArticle() {
                given(articleService.getArticleById(1L)).willReturn(mockArticle);
                CommentRequestDto requestDto = CommentRequestDto.contentOf("test comment");
                Comment comment = Comment.of(account, mockArticle, requestDto);
                given(commentRepository.save(any(Comment.class))).willReturn(comment);

                Comment createdComment = commentService.createCommentOnArticle(1L, account, requestDto);

                SoftAssertions softly = new SoftAssertions();
                softly.assertAll();
                softly.assertThat(createdComment.getAccount()).isEqualTo(account);
                softly.assertThat(createdComment.getContent()).isEqualTo(requestDto.getContent());
                verify(commentRepository).save(any(Comment.class));
            }

        }
    }

    @Nested
    @DisplayName("?????? ??????")
    class Update {
        @Nested
        @DisplayName("?????? ?????? ??????")
        class UpdateSuccess {
            @Test
            @DisplayName("?????? ??????")
            void getCommentsByArticleId() {
                CommentRequestDto requestDto = CommentRequestDto.contentOf("new comment");

                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockCommentList.get(0)));

                commentService.updateComment(1L, requestDto, "jilee");

                verify(commentRepository).save(any(Comment.class));
            }

        }
    }
    @Nested
    @DisplayName("?????? ??????")
    class Delete {
        @Nested
        @DisplayName("?????? ?????? ??????")
        class DeleteSuccess {
            @Test
            @DisplayName("?????? ??????")
            void deleteCommentById() {
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockCommentList.get(0)));

                commentService.deleteCommentById(1L, "jilee");

                verify(commentRepository).delete(any(Comment.class));
            }

        }
    }
}