package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.dto.GetArticles;
import com.example.newsfit.domain.article.dto.GetComment;
import com.example.newsfit.domain.article.entity.*;
import com.example.newsfit.domain.article.repository.ArticleLikesRepository;
import com.example.newsfit.domain.article.repository.ArticleRepository;
import com.example.newsfit.domain.article.repository.CommentLikesRepository;
import com.example.newsfit.domain.article.repository.CommentRepository;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.newsfit.global.util.Utils.jsonObjectParser;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ArticleLikesRepository articleLikesRepository;
    private final CommentLikesRepository commentLikesRepository;

    public GetArticles postArticle(String requestBody) throws ParseException {
        JSONObject jsonObject = jsonObjectParser(requestBody);

        String title = (String) jsonObject.get("title");
        String content = (String) jsonObject.get("content");
        Press press = Press.valueOf(((String) jsonObject.get("press")).toUpperCase());
        Category category = Category.valueOf(((String) jsonObject.get("category")).toUpperCase());
        JSONArray imageArray = (JSONArray) jsonObject.get("image");
        List<String> images = new ArrayList<>();
        String articleSource = (String) jsonObject.get("articleSource");
        String headLine = (String) jsonObject.get("headLine");
        LocalDateTime publishDate = LocalDateTime.parse((String) jsonObject.get("publishDate"));

        if (imageArray != null) {
            for (Object image : imageArray) {
                images.add((String) image);
            }
        }

        Article article = Article.builder()
                .title(title)
                .content(content)
                .press(press)
                .category(category)
                .images(images)
                .articleSource(articleSource)
                .headLine(headLine)
                .publishDate(publishDate)
                .build();

        articleRepository.save(article);

        return GetArticles.of(article);
    }

    public List<GetArticle> getArticles(String category, String press, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Category categoryEnum = null;
        Press pressEnum = null;

        if (!"allCategory".equalsIgnoreCase(category)) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        if (!"allPress".equalsIgnoreCase(press)) {
            try {
                pressEnum = Press.valueOf(press.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        List<Article> articles = new ArrayList<>();

        if (categoryEnum == null && pressEnum == null) {
            articles = articleRepository.findAll(pageable).getContent();
        } else if (categoryEnum == null) {
            articles = articleRepository.findByPress(pressEnum, pageable);
        } else if (pressEnum == null) {
            articles = articleRepository.findByCategory(categoryEnum, pageable);
        } else {
            articles = articleRepository.findByCategoryAndPress(categoryEnum, pressEnum, pageable);
        }

        List<GetArticle> returnArticles = new ArrayList<>();
        for (Article article : articles) {
            GetArticle getArticle = GetArticle.of(article);
            returnArticles.add(getArticle);
        }

        return returnArticles;
    }

    public Boolean removeArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("기사를 찾을 수 없습니다."));

        articleRepository.delete(article);

        return true;
    }

    public GetComment postComment(String articleId, String requestBody) throws ParseException {
        JSONObject jsonObject = jsonObjectParser(requestBody);

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));


        String content = (String) jsonObject.get("content");

        Comment comment = Comment.builder()
                .member(member)
                .article(article)
                .content(content).build();

        return GetComment.of(commentRepository.save(comment));
    }

    public GetArticle getArticle(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        return GetArticle.of(article);
    }

    public Boolean deleteComment(String articleId, String commentId) {
        Comment comment = commentRepository.findById(Long.parseLong(commentId))
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!comment.getMember().equals(member)) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        return comment.deleteComment();
    }

    public Boolean postArticleLikes(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        if (articleLikesRepository.findByMemberAndArticle(member, article).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ARTICLE_LIKE);
        }

        ArticleLike articleLike = ArticleLike.builder()
                .article(article)
                .member(member)
                .build();

        articleLikesRepository.save(articleLike);
        article.addLikeCount();

        return true;
    }

    public Boolean deleteArticleLikes(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Integer> removeLike = articleLikesRepository.removeByMemberAndArticle(member, article);

        if (removeLike.isPresent() && removeLike.get() == 0) {
            throw new CustomException(ErrorCode.ARTICLE_LIKE_NOT_FOUND);
        }

        article.subLikeCount();

        return true;
    }

    public Boolean postCommentLikes(String articleId, String commentId) {
        Comment comment = commentRepository.findById(Long.parseLong(commentId))
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (commentLikesRepository.findByMemberAndComment(member, comment).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_COMMENT_LIKE);
        }

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();

        commentLikesRepository.save(commentLike);
        comment.addLikeCount();

        return true;
    }

    public Boolean deleteCommentLikes(String articleId, String commentId) {
        Comment comment = commentRepository.findById(Long.parseLong(commentId))
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Integer> removeLike = commentLikesRepository.removeByMemberAndComment(member, comment);

        if (removeLike.isPresent() && removeLike.get() == 0) {
            throw new CustomException(ErrorCode.ARTICLE_LIKE_NOT_FOUND);
        }

        comment.subLikeCount();

        return true;
    }
}

