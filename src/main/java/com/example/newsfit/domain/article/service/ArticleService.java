package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.dto.GetArticles;
import com.example.newsfit.domain.article.dto.GetComment;
import com.example.newsfit.domain.article.entity.*;
import com.example.newsfit.domain.article.repository.ArticleLikesRepository;
import com.example.newsfit.domain.article.repository.ArticleRepository;
import com.example.newsfit.domain.article.repository.CommentRepository;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

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

    public GetArticles postArticle(String requestBody) throws ParseException {
        JSONObject jsonObject = jsonObjectParser(requestBody);

        String title = (String) jsonObject.get("title");
        String content = (String) jsonObject.get("content");
        Press press = Press.valueOf(((String) jsonObject.get("press")).toUpperCase());
        Category category = Category.valueOf(((String) jsonObject.get("category")).toUpperCase());

        Article article = Article.builder()
                .title(title)
                .content(content)
                .press(press)
                .category(category)
                .build();

        articleRepository.save(article);

        return GetArticles.of(article);
    }

    public List<Article> getArticles(String category, String press, int page, int size) {
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

        if (categoryEnum == null && pressEnum == null) {
            return articleRepository.findAll(pageable).getContent();
        } else if (categoryEnum == null) {
            return articleRepository.findByPress(pressEnum, pageable);
        } else if (pressEnum == null) {
            return articleRepository.findByCategory(categoryEnum, pageable);
        } else {
            return articleRepository.findByCategoryAndPress(categoryEnum, pressEnum, pageable);
        }
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

        if(!comment.getMember().equals(member)){
            throw new CustomException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        return comment.deleteComment();
    }

    public Boolean postArticleLikes(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findByMemberId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        if(articleLikesRepository.findByMemberAndArticle(member, article).isPresent()){
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
}

