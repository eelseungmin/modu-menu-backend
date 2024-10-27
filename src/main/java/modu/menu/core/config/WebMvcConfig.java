package modu.menu.core.config;

import lombok.RequiredArgsConstructor;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.converter.FoodTypeRequestConverter;
import modu.menu.core.converter.VibeTypeRequestConverter;
import modu.menu.core.interceptor.JwtCheckInterceptor;
import modu.menu.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new VibeTypeRequestConverter());
        registry.addConverter(new FoodTypeRequestConverter());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtCheckInterceptor(userRepository, jwtProvider))
                .order(1)
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
