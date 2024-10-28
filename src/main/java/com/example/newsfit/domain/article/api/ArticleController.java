package com.example.newsfit.domain.article.api;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.dto.GetArticles;
import com.example.newsfit.domain.article.dto.GetComment;
import com.example.newsfit.domain.article.service.ArticleService;
import com.example.newsfit.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.minidev.json.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Article", description = "기사 관련 API")
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @Operation(summary = "뉴스 등록",
            description = """
                    뉴스를 등록하는 API입니다.
                    """)
    @PostMapping
    public SuccessResponse<GetArticles> postArticle(@RequestBody String requestBody) throws ParseException {
        return SuccessResponse.success(articleService.postArticle(requestBody));
    }

    @Operation(summary = "뉴스 조회",
            description = """
                    뉴스 조회 API입니다.
                    
                    파라미터는 다음과 같습니다.
                    category: 조회하고자 하는 기사의 카테고리를 지정합니다.
                    articleCursor: 조회하고자 하는 페이지의 직전 기사의 아이디입니다.
                    size: 한 페이지에 포함될 기사의 개수를 지정합니다.
                    """)
    @GetMapping
    public SuccessResponse<List<GetArticles>> getArticle(@RequestParam(value = "category", required = false, defaultValue = "allCategory") String category,
                                                         @RequestParam(value = "articleCursor", required = false) Long articleCursor,
                                                         @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return SuccessResponse.success(articleService.getArticles(category, articleCursor, size));
    }

    @Operation(summary = "뉴스 검색",
            description = """
                    뉴스 검색 API입니다.

                    파라미터는 다음과 같습니다.
                    keyword: 검색하고자 하는 기사의 키워드를 지정합니다.
                    articleCursor: 조회하고자 하는 페이지의 직전 기사의 아이디입니다.
                    size: 한 페이지에 포함될 기사의 개수를 지정합니다.
                    """)
    @GetMapping("/search")
    public SuccessResponse<List<GetArticles>> searchArticles(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                             @RequestParam(value = "articleCursor", required = false) Long articleCursor,
                                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return SuccessResponse.success(articleService.searchArticles(keyword, articleCursor, size));
    }

    @Operation(summary = "뉴스 기사 단건 조회",
            description = """
                    뉴스 기사 단건 조회 API입니다.
                    """)

    @GetMapping("/{articleId}")
    public SuccessResponse<GetArticle> getArticle(@PathVariable String articleId) {
        return SuccessResponse.success(articleService.getArticle(articleId));
    }

    @Operation(summary = "뉴스 삭제",
            description = """
                    뉴스 삭제 API입니다.
                    """)
    @DeleteMapping("/{articleId}")
    public SuccessResponse<Boolean> deleteArticle(@PathVariable("articleId") String articleId) {
        return SuccessResponse.success(articleService.removeArticle(Long.parseLong(articleId)));
    }

    @Operation(summary = "댓글 작성",
            description = """
                    댓글 작성 API입니다.
                    """)
    @PostMapping("/{articleId}/comments")
    public SuccessResponse<GetComment> postComment(@PathVariable("articleId") String articleId,
                                                   @RequestBody String requestBody) throws ParseException {
        return SuccessResponse.createSuccess(articleService.postComment(articleId, requestBody));
    }

    @Operation(summary = "댓글 삭제",
            description = """
                    댓글 삭제 API입니다.
                    """)
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public SuccessResponse<Boolean> deleteComment(@PathVariable("articleId") String articleId,
                                                  @PathVariable("commentId") String commentId) {
        return SuccessResponse.success(articleService.deleteComment(articleId, commentId));
    }

    @Operation(summary = "뉴스 좋아요",
            description = """
                    뉴스 좋아요 API입니다.
                    """)
    @PostMapping("/{articleId}/likes")
    public SuccessResponse<Boolean> postArticleLikes(@PathVariable("articleId") String articleId) {
        return SuccessResponse.createSuccess(articleService.postArticleLikes(articleId));
    }

    @Operation(summary = "뉴스 좋아요 취소",
            description = """
                    뉴스 좋아요 취소 API입니다.
                    """)
    @DeleteMapping("/{articleId}/likes")
    public SuccessResponse<Boolean> deleteArticleLikes(@PathVariable("articleId") String articleId) {
        return SuccessResponse.success(articleService.deleteArticleLikes(articleId));
    }

    @Operation(summary = "댓글 좋아요",
            description = """
                    댓글 좋아요 API입니다.
                    """)
    @PostMapping("/{articleId}/comments/{commentId}/likes")
    public SuccessResponse<Boolean> postCommentLikes(@PathVariable("articleId") String articleId,
                                                     @PathVariable("commentId") String commentId) {
        return SuccessResponse.createSuccess(articleService.postCommentLikes(articleId, commentId));
    }

    @Operation(summary = "댓글 좋아요 취소",
            description = """
                    댓글 좋아요 취소 API입니다.
                    """)
    @DeleteMapping("/{articleId}/comments/{commentId}/likes")
    public SuccessResponse<Boolean> deleteCommentLikes(@PathVariable("articleId") String articleId,
                                                       @PathVariable("commentId") String commentId) {
        return SuccessResponse.createSuccess(articleService.deleteCommentLikes(articleId, commentId));
    }

    @Operation(summary = "원본 기사 조회",
            description = """
                    원본 기사 조회 API입니다.
                    """)
    @GetMapping("/source/{articleId}")
    public SuccessResponse<String> getArticleSource(
            @PathVariable("articleId") String articleId){
        return SuccessResponse.success(articleService.getArticleSource(articleId));
    }
}
