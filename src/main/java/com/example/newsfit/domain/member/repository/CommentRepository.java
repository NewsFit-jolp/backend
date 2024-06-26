package com.example.newsfit.domain.member.repository;

import com.example.newsfit.domain.member.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
