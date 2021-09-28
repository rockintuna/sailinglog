package me.rockintuna.sailinglog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.sailinglog.domain.Article;
import me.rockintuna.sailinglog.domain.ArticleRequestDto;
import me.rockintuna.sailinglog.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class ArticleControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

    List<Article> articleList = new ArrayList<>();

    @BeforeEach
    private void beforeEach() {
        //given
        articleList.add(Article.from(
                new ArticleRequestDto("test title 1", "tester", "test content 1")));
        articleList.add(Article.from(
                new ArticleRequestDto("test title 2", "tester", "test content 2")));
        articleList.add(Article.from(
                new ArticleRequestDto("test title 3", "tester", "test content 3")));
        articleList.add(Article.from(
                new ArticleRequestDto("test title 4", "tester", "<script>alert('XSS');</script>")));
    }

    @Test
    void getArticlesOrderByCreatedAtDesc() throws Exception {
        //given
        given(articleService.getArticlesOrderByCreatedAtDesc())
                .willReturn(articleList);

        //when
        mvc.perform(MockMvcRequestBuilders.get("/articles")
                        .with(user("jilee").roles("USER")))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("test title 1"))
                .andExpect(jsonPath("$[0].writer").value("tester"))
                .andExpect(jsonPath("$[0].content").value("test content 1"))
                .andExpect(jsonPath("$[3].title").value("test title 4"))
                .andExpect(jsonPath("$[3].writer").value("tester"))
                .andExpect(jsonPath("$[3].content").value("&lt;script&gt;alert('XSS');&lt;/script&gt;"));

        verify(articleService).getArticlesOrderByCreatedAtDesc();
    }

    @Test
    void getArticleById() throws Exception {
        given(articleService.getArticleById(1L))
                .willReturn(articleList.get(0));

        mvc.perform(MockMvcRequestBuilders.get("/articles/1")
                        .with(user("jilee").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("test title 1"))
                .andExpect(jsonPath("$.writer").value("tester"))
                .andExpect(jsonPath("$.content").value("test content 1"));

        verify(articleService).getArticleById(1L);
    }

    @Test
    void getXSSArticleById() throws Exception {
        given(articleService.getArticleById(4L))
                .willReturn(articleList.get(3));

        mvc.perform(MockMvcRequestBuilders.get("/articles/4")
                        .with(user("jilee").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("test title 4"))
                .andExpect(jsonPath("$.writer").value("tester"))
                .andExpect(jsonPath("$.content").value("&lt;script&gt;alert('XSS');&lt;/script&gt;"));

        verify(articleService).getArticleById(4L);
    }

    @Test
    void createArticle() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        given(articleService.createArticle(any(ArticleRequestDto.class)))
                .willReturn(articleList.get(0));

        mvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("test title 1"))
                .andExpect(jsonPath("$.writer").value("tester"))
                .andExpect(jsonPath("$.content").value("test content 1"));

        verify(articleService).createArticle(any(ArticleRequestDto.class));
    }

    @Test
    void updateArticle() throws Exception {
        ArticleRequestDto requestDto =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        String jsonString = objectMapper.writeValueAsString(requestDto);

        given(articleService.updateArticle(eq(1L), any(ArticleRequestDto.class)))
                .willReturn(1L);

        mvc.perform(MockMvcRequestBuilders.put("/articles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString)
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(articleService).updateArticle(eq(1L), any(ArticleRequestDto.class));
    }

    @Test
    void deleteArticleById() throws Exception {
        given(articleService.deleteArticleById(1L))
                .willReturn(1L);

        mvc.perform(MockMvcRequestBuilders.delete("/articles/1")
                        .with(user("jilee").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(articleService).deleteArticleById(1L);
    }
}