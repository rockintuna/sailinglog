package me.rockintuna.sailinglog.repository;

import me.rockintuna.sailinglog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findALlByArticleId(Long article_id);
}
