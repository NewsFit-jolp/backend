package com.example.daycarat.domain.user.api;

import com.example.daycarat.domain.user.dto.GetUserInfo;
import com.example.daycarat.domain.user.service.UserService;
import com.example.daycarat.global.jwt.TokenResponse;
import com.example.daycarat.global.oauth.KakaoUserService;
import com.example.daycarat.global.response.SuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User", description = "유저 관련 API")
@RestController @RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KakaoUserService kakaoUserService;

    @Operation(summary = "카카오 액세스 토큰으로 내부 토큰 발급하기",
            description = """
                    카카오 액세스 토큰으로 내부 토큰을 발급합니다.
                    
                    **상태 코드에 따라 최초 회원가입, 기존 유저 로그인 여부를 알 수 있습니다.**
                    - statusCode가 200인 경우: 기존 유저 로그인
                    - statusCode가 201인 경우: 최초 회원가입
                    
                    개발용 유저 삭제 API를 통해 최초 회원가입이 정상적으로 처리되는지 확인할 수 있습니다.
                  
                    요청값:
                    - (Query Parameter) accessToken: 카카오 인증서버에서 받은 토큰입니다.
                    
                    반환값:
                    - accessToken: 서버 내부에서 발급한 토큰입니다.
                    - refreshToken: 서버 내부에서 발급한 토큰입니다.
                    """)
    @GetMapping("/oauth/kakao")
    public SuccessResponse<TokenResponse> kakaoLogin(@Parameter(name = "accessToken", description = "카카오 인증서버에서 받은 토큰", required = true)
                                        @RequestParam String accessToken) throws JsonProcessingException {
        Pair<TokenResponse, Boolean> pair = kakaoUserService.kakaoLogin(accessToken);

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
    @GetMapping("/userInfo")
    public SuccessResponse<GetUserInfo> getUserInfo() {
        return SuccessResponse.success(userService.getUserInfo());
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
        return SuccessResponse.success(userService.deleteUser());
    }

}
