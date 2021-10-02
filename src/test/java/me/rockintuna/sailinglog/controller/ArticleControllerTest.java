package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.config.exception.ArticleNotFoundException;
import me.rockintuna.sailinglog.model.Article;
import me.rockintuna.sailinglog.dto.ArticleRequestDto;
import me.rockintuna.sailinglog.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
                new ArticleRequestDto("test title 1", "tester", "test content 1")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 2", "tester", "test content 2")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 3", "tester", "test content 3")));
        mockArticleList.add(Article.from(
                new ArticleRequestDto("test title 4", "tester", "<script>alert('XSS');</script>")));
    }

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
                .andExpect(jsonPath("$[0].writer")
                        .value(mockArticleList.get(0).getWriter()))
                .andExpect(jsonPath("$[0].content")
                        .value(mockArticleList.get(0).getContent()))
                .andExpect(jsonPath("$[3].title")
                        .value(mockArticleList.get(3).getTitle()))
                .andExpect(jsonPath("$[3].writer")
                        .value(mockArticleList.get(3).getWriter()))
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
                .andExpect(jsonPath("$.writer").value(mockArticleList.get(0).getWriter()))
                .andExpect(jsonPath("$.content").value(mockArticleList.get(0).getContent()));

        verify(articleService).getArticleById(1L);
    }

    @Test
    @DisplayName("GET /articles/{id} with invalid article ID")
    void getArticleByInvalidId() throws Exception {
        given(articleService.getArticleById(99L))
                .willThrow(new ArticleNotFoundException());

        mvc.perform(MockMvcRequestBuilders.get("/articles/99")
                        .with(user("jilee").roles("USER")))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(articleService).getArticleById(99L);
    }

    @Test
    @DisplayName("GET /articles/{id} by XSS attack")
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
                .andExpect(jsonPath("$.writer")
                        .value(mockArticleList.get(3).getWriter()))
                .andExpect(jsonPath("$.content")
                        .value(mockArticleList.get(3).getContent()
                                .replace("<","&lt;")
                                .replace(">","&gt;")));

        verify(articleService).getArticleById(4L);
    }

    @Test
    @DisplayName("POST /articles")
    void createArticle() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
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
                .andExpect(jsonPath("$.writer").value(requestDto.getWriter()))
                .andExpect(jsonPath("$.content").value(requestDto.getContent()));

        verify(articleService).createArticle(any(ArticleRequestDto.class));
    }

    @Test
    @DisplayName("POST /articles with invalid requestDto")
    void createArticleWithInvalidData() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto)
                        .replace("writer","retirw");

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
    @DisplayName("POST /articles with no CSRF Token")
    void createArticleWithNoCSRFToken() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(articleService, never()).createArticle(any());
    }

    @Test
    @DisplayName("PUT /articles/{id}")
    void updateArticle() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(articleService).updateArticle(eq(1L), any(ArticleRequestDto.class));
    }

    @Test
    @DisplayName("PUT /articles/{id} with invalid article ID")
    void updateArticleByInvalidId() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);
        willThrow(ArticleNotFoundException.class)
                .given(articleService).updateArticle(eq(99L), any(ArticleRequestDto.class));

        mvc.perform(put("/articles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(articleService).updateArticle(eq(99L), any(ArticleRequestDto.class));
    }

    @Test
    @DisplayName("PUT /articles/{id} with null ID")
    void updateArticleByIdNull() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        mvc.perform(put("/articles/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());

        verify(articleService, never()).updateArticle(any(), any());
    }

    @Test
    @DisplayName("PUT /articles/{id} with invalid requestDto")
    void updateArticleWithInvalidData() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto)
                .replace("writer","retirw");

        mvc.perform(put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(articleService, never()).updateArticle(any(), any());
    }

    @Test
    @DisplayName("PUT /articles/{id} with no CSRF Token")
    void updateArticleWithNoCSRFToken() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        mvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(articleService, never()).updateArticle(any(), any());
    }

    @Test
    @DisplayName("DELETE /articles/{id}")
    void deleteArticleById() throws Exception {

        mvc.perform(delete("/articles/1")
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(articleService).deleteArticleById(1L);
    }

    @Test
    @DisplayName("DELETE /articles/{id} with invalid article ID")
    void deleteArticleByInvalidId() throws Exception {
        willThrow(ArticleNotFoundException.class)
                .given(articleService).deleteArticleById(99L);

        mvc.perform(delete("/articles/99")
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(articleService).deleteArticleById(99L);
    }

    @Test
    @DisplayName("DELETE /articles/{id} with null ID")
    void deleteArticleByIdNull() throws Exception {
        mvc.perform(delete("/articles/")
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());

        verify(articleService, never()).deleteArticleById(any());
    }

    @Test
    @DisplayName("DELETE /articles/{id} with no CSRF Token")
    void deleteArticleWithNoCSRFToken() throws Exception {
        mvc.perform(post("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(articleService, never()).deleteArticleById(any());
    }
}