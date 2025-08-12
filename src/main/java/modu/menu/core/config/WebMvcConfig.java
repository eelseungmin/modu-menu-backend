package modu.menu.core.config;

import lombok.RequiredArgsConstructor;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.converter.FoodTypeRequestConverter;
import modu.menu.core.converter.VibeTypeRequestConverter;
import modu.menu.core.interceptor.JwtCheckInterceptor;
import modu.menu.core.interceptor.PerformanceLoggingInterceptor;
import modu.menu.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final VibeTypeRequestConverter vibeTypeRequestConverter;
    private final FoodTypeRequestConverter foodTypeRequestConverter;
    private final JwtCheckInterceptor jwtCheckInterceptor;
    private final PerformanceLoggingInterceptor performanceLoggingInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(vibeTypeRequestConverter);
        registry.addConverter(foodTypeRequestConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceLoggingInterceptor)
                .order(1)
                .excludePathPatterns(
                        "/api-docs/**", // Swagger
                        "/swagger-ui/**", // Swagger
                        "/api/slack",
                        "/api/health-check"
                );
        registry.addInterceptor(jwtCheckInterceptor)
                .order(2)
                .excludePathPatterns(
                        "/api-docs/**", // Swagger
                        "/swagger-ui/**", // Swagger
                        "/api/health-check",
                        "/api/user",
                        "/api/user/login",
                        "/api/place{?*}",
                        "/api/slack",
                        "/api/category"
                );
    }
}
