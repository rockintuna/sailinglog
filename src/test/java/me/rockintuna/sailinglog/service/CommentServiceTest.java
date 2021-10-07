package me.rockintuna.sailinglog.service;

import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.model.Comment;
import me.rockintuna.sailinglog.repository.CommentRepository;
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
    @DisplayName("댓글 조회")
    class Read {
        @Nested
        @DisplayName("댓글 조회 성공")
        class ReadSuccess {
            @Test
            @DisplayName("댓글 목록 조회")
            void getCommentsByArticleId() {
                given(commentRepository.findALlByArticleId(1L)).willReturn(mockCommentList);

                List<Comment> commentList = commentService.getCommentsByArticleId(1L);

                SoftAssertions softly = new SoftAssertions();
                softly.assertThat(commentList.size()).isEqualTo(mockCommentList.size());
                softly.assertThat(commentList.get(0).getAccount()).isEqualTo(mockCommentList.get(0).getAccount());
                softly.assertThat(commentList.get(0).getContent()).isEqualTo(mockCommentList.get(0).getContent());
                softly.assertThat(commentList.get(3).getAccount()).isEqualTo(mockCommentList.get(3).getAccount());
                softly.assertThat(commentList.get(3).getContent()).isEqualTo(mockCommentList.get(3).getContent());
                softly.assertAll();
                verify(commentRepository).findALlByArticleId(1L);
            }

        }
    }
    @Nested
    @DisplayName("댓글 생성")
    class Create {
        @Nested
        @DisplayName("댓글 생성 성공")
        class CreateSuccess {
            @Test
            @DisplayName("댓글 생성")
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
    @DisplayName("댓글 변경")
    class Update {
        @Nested
        @DisplayName("댓글 변경 성공")
        class UpdateSuccess {
            @Test
            @DisplayName("댓글 변경")
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
    @DisplayName("댓글 삭제")
    class Delete {
        @Nested
        @DisplayName("댓글 삭제 성공")
        class DeleteSuccess {
            @Test
            @DisplayName("댓글 삭제")
            void deleteCommentById() {
                given(commentRepository.findById(1L))
                        .willReturn(Optional.of(mockCommentList.get(0)));

                commentService.deleteCommentById(1L, "jilee");

                verify(commentRepository).delete(any(Comment.class));
            }

        }
    }
}