package com.example.newsfit.domain.article.repository;

import com.example.newsfit.domain.article.entity.Comment;
import com.example.newsfit.domain.article.entity.CommentLike;
import com.example.newsfit.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);

    Optional<Integer> removeByMemberAndComment(Member member, Comment comment);
}
