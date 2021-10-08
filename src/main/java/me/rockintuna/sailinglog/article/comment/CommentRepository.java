package me.rockintuna.sailinglog.article.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findALlByArticleIdOrderByCreatedAtDesc(Long article_id);
}
