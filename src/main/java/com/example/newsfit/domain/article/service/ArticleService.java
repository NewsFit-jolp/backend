package com.example.newsfit.domain.article.service;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.dto.GetArticles;
import com.example.newsfit.domain.article.dto.GetComment;
import com.example.newsfit.domain.article.dto.LangChainRequest;
import com.example.newsfit.domain.article.entity.*;
import com.example.newsfit.domain.article.repository.*;
import com.example.newsfit.domain.member.entity.Member;
import com.example.newsfit.domain.member.repository.MemberRepository;
import com.example.newsfit.global.error.exception.CustomException;
import com.example.newsfit.global.error.exception.ErrorCode;
import com.example.newsfit.global.util.RecommenderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final ArticleSourceRepository articleSourceRepository;

    private final RestTemplate restTemplate;
    private final RecommenderUtils recommenderUtils;

    @Value("${cloud.aws.lambda.langchain.endpoint}")
    private String langChainEndpoint;

    public GetArticles postArticle(String requestBody) throws ParseException {
        JSONObject jsonObject = jsonObjectParser(requestBody);

        String title = (String) jsonObject.get("title");
        String content = (String) jsonObject.get("content");
        Press press = Press.valueOf(((String) jsonObject.get("press")).toUpperCase());
        Category category = Category.fromDisplayName((String) jsonObject.get("category"));
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

    public List<GetArticles> getArticles(String category, Long articleCursor, int size) {
        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category categoryEnum = null;

        if (!"allCategory".equalsIgnoreCase(category)) {
            try {
                categoryEnum = Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        List<Press> preferredPress = member.getPreferredPress();
        List<Article> articles = getArticlesByCursor(categoryEnum, articleCursor, size, preferredPress);
        List<GetArticles> returnArticles = new ArrayList<>();
        for (Article article : articles) {
            GetArticles getArticle = GetArticles.of(article);
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

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
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

    public GetArticle getArticle(String articleId) throws JsonProcessingException {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        if (article.getContent() == null) {
            article.summaryArticle(summaryArticle(article));
        }

        Boolean isLikedArticle = articleLikesRepository.existsByMember_OAuthIdAndArticle(SecurityContextHolder.getContext().getAuthentication().getName(), article);
        return GetArticle.of(article, isLikedArticle);
    }

    private String summaryArticle(Article article) throws JsonProcessingException {
        String url = article.getArticleSource();
        ArticleSource articleSource = articleSourceRepository.findByUrl(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(new LangChainRequest(articleSource.getContent()));
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                langChainEndpoint,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return responseMap.get("content").toString();
        } else {
            throw new RuntimeException("Failed to send POST request: " + response.getStatusCode());
        }
    }

    public Boolean deleteComment(String articleId, String commentId) {
        Comment comment = commentRepository.findById(Long.parseLong(commentId))
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!comment.getMember().equals(member)) {
            throw new CustomException(ErrorCode.COMMENT_DELETE_FORBIDDEN);
        }

        return comment.deleteComment();
    }

    public Boolean postArticleLikes(String articleId) {
        Article article = articleRepository.findById(Long.parseLong(articleId))
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
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

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
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

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
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

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Optional<Integer> removeLike = commentLikesRepository.removeByMemberAndComment(member, comment);

        if (removeLike.isPresent() && removeLike.get() == 0) {
            throw new CustomException(ErrorCode.ARTICLE_LIKE_NOT_FOUND);
        }

        comment.subLikeCount();

        return true;
    }

    private List<Article> getArticlesByCursor(Category category, Long articleId, int size, List<Press> preferredPress) {
        Pageable pageable = PageRequest.of(0, size);
        if (category == null) {
            return articleId == null ?
                    articleRepository.findByPressInOrderByArticleIdDesc(preferredPress, pageable) :
                    articleRepository.findByArticleIdLessThanAndPressInOrderByArticleIdDesc(articleId, preferredPress, pageable);
        } else {
            return articleId == null ?
                    articleRepository.findByCategoryAndPressInOrderByArticleIdDesc(category, preferredPress, pageable) :
                    articleRepository.findByArticleIdLessThanAndCategoryAndPressInOrderByArticleIdDesc(articleId, category, preferredPress, pageable);
        }
    }

    public List<GetArticles> searchArticles(String keyword, Long articleId, int size) {
        List<Article> articles = searchArticlesByCursor(keyword, articleId, size);
        List<GetArticles> returnArticles = new ArrayList<>();
        for (Article article : articles) {
            GetArticles getArticle = GetArticles.of(article);
            returnArticles.add(getArticle);
        }

        return returnArticles;
    }

    private List<Article> searchArticlesByCursor(String keyword, Long articleId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        return articleId == null ?
                articleRepository.findAllByTitleOrCategoryContaining(keyword, pageable) :
                articleRepository.findByTitleOrCategoryContaining(keyword, articleId, pageable);
    }

    public String rateArticle(Long articleId, String requestBody) throws ParseException, JsonProcessingException {
        JSONObject jsonObject = jsonObjectParser(requestBody);
        Integer preference = (Integer) jsonObject.get("preference");

        Member member = memberRepository.findByOAuthId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return recommenderUtils.rateArticle(articleId, member.getId(), preference);
    }
}

