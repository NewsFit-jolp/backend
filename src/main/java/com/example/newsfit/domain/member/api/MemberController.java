package com.example.newsfit.domain.member.api;

import com.example.newsfit.domain.member.dto.GetMemberInfo;
import com.example.newsfit.domain.member.service.MemberService;
import com.example.newsfit.global.jwt.TokenResponse;
import com.example.newsfit.global.oauth.GoogleMemberService;
import com.example.newsfit.global.oauth.KakaoMemberService;
import com.example.newsfit.global.oauth.NaverMemberService;
import com.example.newsfit.global.response.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "유저 관련 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final KakaoMemberService kakaoMemberService;
    private final GoogleMemberService googleMemberService;
    private final NaverMemberService naverMemberService;


    @Operation(summary = "카카오 인증 서버를 통한 로그인",
            description = """
                    카카오 인증 서버를 통해 로그인합니다.
                                        
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                                        
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                                      
                    요청값:
                    - (Query Parameter) code: 카카오 인증서버에서 받은 인증 코드값입니다.
                                        
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)

    @GetMapping("/oauth/kakao")
    public SuccessResponse<TokenResponse> kakaoLogin(@Parameter(name = "code", description = "카카오 인증서버에서 받은 인증 코드", required = true)
                                                     @RequestParam String code) throws JsonProcessingException {
        String accessToken = kakaoMemberService.getAccessToken(code);

        Pair<TokenResponse, Boolean> pair = kakaoMemberService.kakaoLogin(accessToken);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }

    }

    @Operation(summary = "구글 인증 서버를 통한 로그인",
            description = """
                    구글 인증 서버를 통해 로그인합니다.
                                        
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                                        
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                                      
                    요청값:
                    - (Query Parameter) code: 구글 인증서버에서 받은 인증 코드값입니다.
                                        
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)

    @GetMapping("/oauth/google")
    public SuccessResponse<TokenResponse> googleLogin(@Parameter(name = "code", description = "카카오 인증서버에서 받은 인증 코드", required = true)
                                                      @RequestParam String code) throws JsonProcessingException {

        String accessToken = googleMemberService.getAccessToken(code);
        Pair<TokenResponse, Boolean> pair = googleMemberService.googleLogin(accessToken);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }
    }

    @Operation(summary = "네이버 인증 서버를 통한 로그인",
            description = """
                    네이버 인증 서버를 통해 로그인합니다.
                                        
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                                        
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                                      
                    요청값:
                    - (Query Parameter) code: 네이버 인증서버에서 받은 인증 코드값입니다.
                                        
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)

    @GetMapping("/oauth/naver")
    public SuccessResponse<TokenResponse> naverLogin(@Parameter(name = "code", description = "카카오 인증서버에서 받은 인증 코드", required = true)
                                                     @RequestParam String code,
                                                     @Parameter(name = "state", description = "csrf 방지용 상태 토큰값", required = true)
                                                     @RequestParam String state) throws JsonProcessingException {

        String accessToken = naverMemberService.getAccessToken(code, state);
        Pair<TokenResponse, Boolean> pair = naverMemberService.naverLogin(accessToken);

        if (pair.getRight()) {
            return SuccessResponse.createSuccess(pair.getLeft());
        } else {
            return SuccessResponse.success(pair.getLeft());
        }
    }


    @Operation(summary = "유저 정보 조회하기",
            description = """
                    유저 정보를 조회합니다.
                    """)
    @GetMapping("/info")
    public SuccessResponse<GetMemberInfo> getUserInfo() {
        return SuccessResponse.success(memberService.getUserInfo());
    }

    @Operation(summary = "(개발용) 유저 삭제하기",
            description = """
                    개발용 API입니다. 로그인 한 유저를 삭제합니다.
                                        
                    **테스트용으로만 사용해야 합니다.**
                                        
                    에피소드, 활동 태그, 보석함 등의 유저 관련 모든 데이터가 삭제됩니다.
                                        
                    # **해당 API는 soft delete 하지 않고 데이터를 직접 삭제합니다. 사용에 주의하세요**
                                        
                    """)
    @DeleteMapping("/delete")
    public SuccessResponse<Boolean> deleteUser() {
        return SuccessResponse.success(memberService.deleteUser());
    }

}
