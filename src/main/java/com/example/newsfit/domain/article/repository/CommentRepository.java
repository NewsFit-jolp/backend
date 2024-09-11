package com.example.newsfit.domain.article.repository;

import com.example.newsfit.domain.article.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
