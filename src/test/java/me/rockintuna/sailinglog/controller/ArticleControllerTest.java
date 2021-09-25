package me.rockintuna.sailinglog.controller;

import me.rockintuna.sailinglog.domain.Article;
import me.rockintuna.sailinglog.domain.ArticleRequestDto;
import me.rockintuna.sailinglog.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class ArticleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    @Test
    void getArticlesOrderByCreatedAtDesc() throws Exception {
        //given
        ArticleRequestDto articleRequestDto1 =
                new ArticleRequestDto("test title 1", "tester", "test content 1");
        ArticleRequestDto articleRequestDto2 =
                new ArticleRequestDto("test title 2", "tester", "test content 2");
        ArticleRequestDto articleRequestDto3 =
                new ArticleRequestDto("test title 3", "tester", "test content 3");
        List<Article> articleList = new ArrayList<>();
        articleList.add(Article.from(articleRequestDto1));
        articleList.add(Article.from(articleRequestDto2));
        articleList.add(Article.from(articleRequestDto3));

        given(articleService.getArticlesOrderByCreatedAtDesc())
                .willReturn(articleList);

        //when
        mvc.perform(MockMvcRequestBuilders.get("/articles"))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("test title 1"))
                .andExpect(jsonPath("$[0].writer").value("tester"))
                .andExpect(jsonPath("$[0].content").value("test content 1"))
                .andExpect(jsonPath("$[2].title").value("test title 3"))
                .andExpect(jsonPath("$[2].writer").value("tester"))
                .andExpect(jsonPath("$[2].content").value("test content 3"));
    }

    @Test
    void getArticleById() {
    }

    @Test
    void createArticle() {
    }

    @Test
    void updateArticle() {
    }

    @Test
    void deleteArticleById() {
    }
}