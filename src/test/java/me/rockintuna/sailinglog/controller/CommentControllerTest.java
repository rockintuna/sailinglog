package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.dto.AccountRequestDto;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.dto.CommentRequestDto;
import me.rockintuna.sailinglog.model.Account;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.model.Comment;
import me.rockintuna.sailinglog.service.CommentService;
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

    List<Comment> mockCommentList = new ArrayList<>();
    Article mockArticle;

    @BeforeEach
    private void beforeEach() {
        Account account = Account.from(AccountRequestDto.of("jilee", "password", "password"));
        mockArticle = Article.from(
                ArticleRequestDto.of("test title 1", "tester", "test content 1"));

        mockCommentList.add(Comment.of(account, CommentRequestDto.of(mockArticle, "test comments")));
    }

    @Nested
    @DisplayName("Get 요청")
    class Get{
        @Nested
        @DisplayName("Get 요청 성공")
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
    @DisplayName("Post 요청")
    class Post{
        @Nested
        @DisplayName("Post 요청 성공")
        class PostSuccess{
            @Test
            public void createCommentOnArticle() throws Exception {
                CommentRequestDto requestDto =
                        CommentRequestDto.of(mockArticle, "test comment2");
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
    @DisplayName("Put 요청")
    class Put{
        @Nested
        @DisplayName("Put 요청 성공")
        class PutSuccess{
            @Test
            public void updateComment() throws Exception {
                CommentRequestDto requestDto =
                        CommentRequestDto.of(mockArticle, "new comment");
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
    @DisplayName("Delete 요청")
    class Delete{
        @Nested
        @DisplayName("Delete 요청 성공")
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