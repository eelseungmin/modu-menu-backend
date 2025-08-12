package modu.menu.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class PerformanceLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute(START_TIME);
        long elapsedTime = System.currentTimeMillis() - startTime;

        long queryCount = QueryCountHolder.get("ProxyDataSource").getTotal();
        long totalQueryTime = QueryCountHolder.get("ProxyDataSource").getTime();

        log.info("uri: {}, method: {}, 요청 처리 시간: {}ms, 쿼리 실행 개수: {}, 쿼리 실행 시간: {}ms",
                request.getRequestURI(),
                request.getMethod(),
                elapsedTime,
                queryCount,
                totalQueryTime);

        QueryCountHolder.clear();
    }
}
