package modu.menu.core.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.exception.Exception401;
import modu.menu.core.response.ErrorMessage;
import modu.menu.domain.User;
import modu.menu.domain.UserStatus;
import modu.menu.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class JwtCheckInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader(jwtProvider.ACCESS_HEADER);
        if (accessToken == null || accessToken.isBlank()) {
            throw new Exception401(ErrorMessage.EMPTY_TOKEN);
        }

        String accessTokenValue = accessToken.replace(jwtProvider.TOKEN_PREFIX, "");

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
            request.setAttribute("userId", userId);
        } catch (TokenExpiredException e) {
            throw new Exception401(ErrorMessage.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {
            throw new Exception401(ErrorMessage.TOKEN_VERIFICATION_FAIL);
        }

        return true;
    }
}
