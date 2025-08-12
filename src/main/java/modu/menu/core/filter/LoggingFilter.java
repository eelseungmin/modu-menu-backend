package modu.menu.core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.UUID;

// 요청과 응답을 로그에 기록한다.
@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 비즈니스 로직 진입 전
        // MDC에 요청을 구분하기 위한 UUID 추가, NginX에서 생성한 request id를 받아오고, 없을 경우 UUID를 생성해서 추가한다.
        String requestId = ((HttpServletRequest) request).getHeader("X-requestID");
        MDC.put("request_id", Objects.toString(requestId, UUID.randomUUID().toString()));

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, response);

        // Swagger 관련 API는 로그를 기록하지 않는다.
        if (req.getRequestURI().startsWith("/api-docs") || req.getRequestURI().startsWith("/swagger-ui")) {
            return;
        }

        // 비즈니스 로직 진입 후
        // request
        Enumeration<String> headerNames = req.getHeaderNames();
        StringBuilder headerValues = new StringBuilder();

        headerNames.asIterator().forEachRemaining(headerKey -> {
            String headerValue = req.getHeader(headerKey);
            headerValues
                    .append("[")
                    .append(headerKey)
                    .append(": ")
                    .append(headerValue)
                    .append("] ");
        });
        String requestBody = req.getContentAsByteArray() + "";
        String requestURI = req.getRequestURI();
        String method = req.getMethod();
        log.info(">>>>> uri: {}, method: {}, header: {}, body: {}", requestURI, method, headerValues, requestBody);

        // response
        StringBuilder responseHeaderValues = new StringBuilder();
        resp.getHeaderNames().forEach(headerKey -> {
            String headerValue = resp.getHeader(headerKey);
            responseHeaderValues
                    .append("[")
                    .append(headerKey)
                    .append(": ")
                    .append(headerValue)
                    .append("] ");
        });
        String responseBody = resp.getContentAsByteArray() + "";
        log.info("<<<<< uri: {}, method: {}, header: {}, body: {}", requestURI, method, responseHeaderValues, responseBody);

        // 이게 있어야 response가 비어있는 채로 전달되지 않는다.
        resp.copyBodyToResponse();

        MDC.clear();
    }
}
