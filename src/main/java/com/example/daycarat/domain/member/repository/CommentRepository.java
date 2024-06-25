package com.example.daycarat.domain.member.repository;

import com.example.daycarat.domain.member.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
