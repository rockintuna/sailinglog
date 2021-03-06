package me.rockintuna.sailinglog.global.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.article.comment.CommentController;
import me.rockintuna.sailinglog.account.AccountRequestDto;
import me.rockintuna.sailinglog.article.ArticleRequestDto;
import me.rockintuna.sailinglog.article.comment.CommentRequestDto;
import me.rockintuna.sailinglog.account.Account;
import me.rockintuna.sailinglog.article.Article;
import me.rockintuna.sailinglog.article.comment.Comment;
import me.rockintuna.sailinglog.article.comment.CommentService;
import me.rockintuna.sailinglog.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private List<Comment> mockCommentList = new ArrayList<>();
    private Article mockArticle;

    @BeforeEach
    private void beforeEach() {
        Account account = Account.from(AccountRequestDto.of("jilee", "password", "password"));
        mockArticle = Article.from(
                ArticleRequestDto.of("test title 1", "tester", "test content 1"));

        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments1")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments2")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments3")));
        mockCommentList.add(Comment.of(account, mockArticle, CommentRequestDto.contentOf("test comments4")));
    }

    @Nested
    @DisplayName("Get ??????")
    class Get{
        @Nested
        @DisplayName("Get ?????? ??????")
        class GetSuccess{
            @Test
            public void getCommentsByArticleId() throws Exception {
                mvc.perform(MockMvcRequestBuilders.get("/articles/1/comments"))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("Post ??????")
    class Post{
        @Nested
        @DisplayName("Post ?????? ??????")
        class PostSuccess{
            @Test
            public void createCommentOnArticle() throws Exception {
                CommentRequestDto requestDto =
                        CommentRequestDto.contentOf("test comment2");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles/1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }
    @Nested
    @DisplayName("Put ??????")
    class Put{
        @Nested
        @DisplayName("Put ?????? ??????")
        class PutSuccess{
            @Test
            public void updateComment() throws Exception {
                CommentRequestDto requestDto =
                        CommentRequestDto.contentOf("new comment");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1/comments/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString)
                            .with(user("jilee").roles("USER"))
                        .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }
    @Nested
    @DisplayName("Delete ??????")
    class Delete{
        @Nested
        @DisplayName("Delete ?????? ??????")
        class DeleteSuccess{
            @Test
            public void deleteCommentById() throws Exception {
                mvc.perform(delete("/articles/1/comments/1")
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }


}