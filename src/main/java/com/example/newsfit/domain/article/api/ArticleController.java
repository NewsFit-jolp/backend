package com.example.newsfit.domain.article.api;

import com.example.newsfit.domain.article.dto.GetArticle;
import com.example.newsfit.domain.article.entity.Article;
import com.example.newsfit.domain.article.service.ArticleService;
import com.example.newsfit.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.minidev.json.parser.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
    public SuccessResponse<GetArticle> postArticle(@RequestBody String requestBody) throws ParseException {
        return SuccessResponse.success(articleService.postArticle(requestBody));
    }

    @Operation(summary = "뉴스 조회",
            description = """
                    뉴스 조회 API입니다.
                    
                    파라미터는 다음과 같습니다.
                    category: 조회하고자 하는 기사의 카테고리를 지정합니다.
                    press: 조회하고자 하는 기사의 언론사를 지정합니다.
                    page: 페이지 번호를 지정합니다.
                    size: 한 페이지에 포함될 기사의 개수를 지정합니다.
                    """)
    @GetMapping
    public SuccessResponse<List<Article>> getArticle(@RequestParam(value = "category", required = false, defaultValue = "allCategory") String category,
                                                     @RequestParam(value = "press", required = false, defaultValue = "allPress") String press,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return SuccessResponse.success(articleService.getArticles(category, press, page, size));
    }
}
