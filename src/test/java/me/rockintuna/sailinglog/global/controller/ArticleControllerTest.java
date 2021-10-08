package me.rockintuna.sailinglog.global.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.article.ArticleController;
import me.rockintuna.sailinglog.config.exception.ArticleNotFoundException;
import me.rockintuna.sailinglog.config.exception.PermissionDeniedException;
import me.rockintuna.sailinglog.article.Article;
import me.rockintuna.sailinglog.article.ArticleRequestDto;
import me.rockintuna.sailinglog.article.ArticleService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

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
    @DisplayName("Get 요청")
    class Get {
        @Nested
        @DisplayName("Get 요청 성공")
        class GetSuccess {
            @Test
            @DisplayName("GET /articles")
            void getArticlesOrderByCreatedAtDesc() throws Exception {
                //given
                given(articleService.getArticlesOrderByCreatedAtDesc())
                        .willReturn(mockArticleList);

                //when
                mvc.perform(MockMvcRequestBuilders.get("/articles")
                                .with(user("jilee").roles("USER")))
                        .andDo(print())

                        //then
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].title")
                                .value(mockArticleList.get(0).getTitle()))
                        .andExpect(jsonPath("$[0].account")
                                .value(mockArticleList.get(0).getAccount()))
                        .andExpect(jsonPath("$[0].content")
                                .value(mockArticleList.get(0).getContent()))
                        .andExpect(jsonPath("$[3].title")
                                .value(mockArticleList.get(3).getTitle()))
                        .andExpect(jsonPath("$[3].account")
                                .value(mockArticleList.get(3).getAccount()))
                        .andExpect(jsonPath("$[3].content")
                                .value(mockArticleList.get(3).getContent()
                                        .replace("<","&lt;")
                                        .replace(">","&gt;")));

                verify(articleService).getArticlesOrderByCreatedAtDesc();
            }

            @Test
            @DisplayName("GET /articles/{id}")
            void getArticleById() throws Exception {
                given(articleService.getArticleById(1L))
                        .willReturn(mockArticleList.get(0));

                mvc.perform(MockMvcRequestBuilders.get("/articles/1")
                                .with(user("jilee").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title").value(mockArticleList.get(0).getTitle()))
                        .andExpect(jsonPath("$.account").value(mockArticleList.get(0).getAccount()))
                        .andExpect(jsonPath("$.content").value(mockArticleList.get(0).getContent()));

                verify(articleService).getArticleById(1L);
            }

            @Test
            @DisplayName("GET /articles/{id} XSS 치환")
            void getXSSArticleById() throws Exception {
                given(articleService.getArticleById(4L))
                        .willReturn(mockArticleList.get(3));

                mvc.perform(MockMvcRequestBuilders.get("/articles/4")
                                .with(user("jilee").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title")
                                .value(mockArticleList.get(3).getTitle()))
                        .andExpect(jsonPath("$.account")
                                .value(mockArticleList.get(3).getAccount()))
                        .andExpect(jsonPath("$.content")
                                .value(mockArticleList.get(3).getContent()
                                        .replace("<","&lt;")
                                        .replace(">","&gt;")));

                verify(articleService).getArticleById(4L);
            }
        }
        @Nested
        @DisplayName("Get 요청 실패")
        class GetFail {
            @Test
            @DisplayName("GET /articles/{id} 없는 게시글 ID")
            void getArticleByInvalidId() throws Exception {
                given(articleService.getArticleById(99L))
                        .willThrow(new ArticleNotFoundException());

                mvc.perform(MockMvcRequestBuilders.get("/articles/99")
                                .with(user("jilee").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isNotFound());

                verify(articleService).getArticleById(99L);
            }
        }
    }
    @Nested
    @DisplayName("Post 요청")
    class Post {
        @Nested
        @DisplayName("Post 요청 성공")
        class PostSuccess {
            @Test
            @DisplayName("POST /articles")
            void createArticle() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                given(articleService.createArticle(any(ArticleRequestDto.class)))
                        .willReturn(mockArticleList.get(0));

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.title").value(requestDto.getTitle()))
                        .andExpect(jsonPath("$.account").value(requestDto.getAccount()))
                        .andExpect(jsonPath("$.content").value(requestDto.getContent()));

                verify(articleService).createArticle(any(ArticleRequestDto.class));
            }
        }
        @Nested
        @DisplayName("Post 요청 실패")
        class PostFail {
            @Test
            @DisplayName("POST /articles 제목 null")
            void createArticleWithTitleNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of(null, "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 제목 공백")
            void createArticleWithTitleEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 작성자 null")
            void createArticleWithWirterNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", null, "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 작성자 null")
            void createArticleWithWirterEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 내용 null")
            void createArticleWithContentNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "tester", null);
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 내용 공백")
            void createArticleWithContentEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "tester", "");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 잘못된 Json 데이터")
            void createArticleWithInvalidData() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto)
                        .replace("account","retirw");

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles CSRF Token 없음")
            void createArticleWithNoCSRFToken() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString))
                        .andDo(print())
                        .andExpect(status().isForbidden());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("POST /articles 로그인 안함")
            void createArticleUnAuthenticated() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());

                verify(articleService, never()).createArticle(any());
            }
        }
    }
    @Nested
    @DisplayName("Put 요청")
    class Put {
        @Nested
        @DisplayName("Put 요청 성공")
        class PutSuccess {
            @Test
            @DisplayName("PUT /articles/{id}")
            void updateArticle() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk());

                verify(articleService).updateArticle(eq(1L), any(ArticleRequestDto.class));
            }
        }
        @Nested
        @DisplayName("Put 요청 실패")
        class PutFail {
            @Test
            @DisplayName("PUT /articles/{id} 없는 게시글 ID")
            void updateArticleByInvalidId() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);
                willThrow(ArticleNotFoundException.class)
                        .given(articleService).updateArticle(eq(99L), any(ArticleRequestDto.class));

                mvc.perform(put("/articles/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isNotFound());

                verify(articleService).updateArticle(eq(99L), any(ArticleRequestDto.class));
            }

            @Test
            @DisplayName("PUT /articles/{id} ID null")
            void updateArticleByIdNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isMethodNotAllowed());

                verify(articleService, never()).updateArticle(any(), any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 제목 null")
            void createArticleWithTitleNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of(null, "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 제목 공백")
            void createArticleWithTitleEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 작성자 null")
            void createArticleWithWirterNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", null, "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 작성자 null")
            void createArticleWithWirterEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 내용 null")
            void createArticleWithContentNull() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "tester", null);
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 내용 공백")
            void createArticleWithContentEmptySpace() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("title", "tester", "");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).createArticle(any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 잘못된 Json 데이터")
            void updateArticleWithInvalidData() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto)
                        .replace("account","retirw");

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isBadRequest());

                verify(articleService, never()).updateArticle(any(), any());
            }

            @Test
            @DisplayName("PUT /articles/{id} CSRF Token 없음")
            void updateArticleWithNoCSRFToken() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(post("/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(user("tester").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isForbidden());

                verify(articleService, never()).updateArticle(any(), any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 로그인 안함")
            void updateArticleUnauthenticated() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());

                verify(articleService,never()).updateArticle(any(), any());
            }

            @Test
            @DisplayName("PUT /articles/{id} 권한 없음")
            void updateArticleHasNoGranted() throws Exception {
                ArticleRequestDto requestDto =
                        ArticleRequestDto.of("test title 1", "tester", "test content 1");
                String jsonString = objectMapper.writeValueAsString(requestDto);

                mvc.perform(put("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonString)
                                .with(csrf())
                                .with(user("demo").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isForbidden());

                verify(articleService,never()).updateArticle(any(), any());
            }
        }
    }
    @Nested
    @DisplayName("Delete 요청")
    class Delete {
        @Nested
        @DisplayName("Delete 요청 성공")
        class DeleteSuccess {
            @Test
            @DisplayName("DELETE /articles/{id}")
            void deleteArticleById() throws Exception {

                mvc.perform(delete("/articles/1")
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isOk());

                verify(articleService).deleteArticle(1L, "jilee");
            }
        }
        @Nested
        @DisplayName("Delete 요청 실패")
        class DeleteFail {
            @Test
            @DisplayName("DELETE /articles/{id} 없는 게시글 ID")
            void deleteArticleByInvalidId() throws Exception {
                willThrow(ArticleNotFoundException.class)
                        .given(articleService).deleteArticle(99L, "jilee");

                mvc.perform(delete("/articles/99")
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isNotFound());

                verify(articleService).deleteArticle(99L, "jilee");
            }

            @Test
            @DisplayName("DELETE /articles/{id} ID null")
            void deleteArticleByIdNull() throws Exception {
                mvc.perform(delete("/articles/")
                                .with(user("jilee").roles("USER"))
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().isMethodNotAllowed());

                verify(articleService, never()).deleteArticle(any(), any());
            }

            @Test
            @DisplayName("DELETE /articles/{id} CSRF Token 없음")
            void deleteArticleWithNoCSRFToken() throws Exception {
                mvc.perform(post("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isForbidden());

                verify(articleService, never()).deleteArticle(any(), any());
            }

            @Test
            @DisplayName("DELETE /articles/{id} 로그인 안함")
            void deleteArticleById() throws Exception {

                mvc.perform(delete("/articles/1")
                                .with(csrf()))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());

                verify(articleService, never()).deleteArticle(any(), any());
            }

            @Test
            @DisplayName("DELETE /articles/{id} 권한 없음")
            void updateArticleHasNoGranted() throws Exception {

                willThrow(PermissionDeniedException.class)
                        .given(articleService).deleteArticle(1L,"jilee");

                mvc.perform(delete("/articles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(user("jilee").roles("USER")))
                        .andDo(print())
                        .andExpect(status().isForbidden());

                verify(articleService,never()).updateArticle(any(), any());
            }
        }
    }
















}