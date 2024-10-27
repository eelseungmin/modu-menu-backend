package modu.menu.core.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.exception.Exception401;
import modu.menu.core.exception.Exception500;
import modu.menu.core.response.ErrorMessage;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.repository.UserRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String requestMethod = req.getMethod();
        String requestUri = req.getRequestURI();

        // 토큰 인증을 필요로 하지 않는 요청들은 별도의 필터링 없이 통과시킨다.
        if (!isJwtURI(requestMethod, requestUri)) {
            chain.doFilter(req, resp);
            return;
        }

        String accessToken = req.getHeader(jwtProvider.ACCESS_HEADER);
        if (accessToken == null || accessToken.isBlank()) {
            throw new Exception401(ErrorMessage.EMPTY_TOKEN);
        }
        String accessTokenValue = accessToken.replace(jwtProvider.TOKEN_PREFIX, "");

        // 토큰을 복호화한 뒤 DB의 회원 데이터와 비교한다.
        try {
            DecodedJWT decodedJWT = jwtProvider.verifyAccessToken(accessTokenValue);
            Long userId = Long.parseLong(decodedJWT.getSubject());

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new Exception401(ErrorMessage.NOT_EXIST_USER_TOKEN)
            );
            if (!user.getStatus().equals(UserStatus.ACTIVE)) {
                throw new Exception401(ErrorMessage.NOT_ACTIVE_USER_TOKEN);
            }

            // 권한 체크에 사용할 수도 있으므로 회원 id를 request에 담는다.
            req.setAttribute("userId", userId);

            chain.doFilter(req, resp);
        } catch (SignatureVerificationException e) { // 토큰 검증 실패 시
            throw new Exception401(ErrorMessage.TOKEN_VERIFICATION_FAIL);
        } catch (TokenExpiredException e) { // 만료된 토큰일 시
            throw new Exception401(ErrorMessage.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new Exception500(ErrorMessage.CHECK_STACK_TRACE);
        }
    }

    // JWT 인증을 적용할지 URI를 통해 판단한다.
    private boolean isJwtURI(String method, String uri) {
        return !uri.startsWith("/api-docs")
                && !uri.startsWith("/swagger-ui")
                && !(method.equals("POST") && uri.equals("/api/user"))
                && !(method.equals("POST") && uri.equals("/api/user/login"))
                && !(method.equals("GET") && uri.equals("/api/health-check"))
                && !(method.equals("GET") && uri.startsWith("/api/place"));
    }
}
