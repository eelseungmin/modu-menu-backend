package modu.menu.core.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceInterceptor {

    @Around("execution(* modu.menu.repository.PlaceJdbcRepositoryImpl.insertDummyData(..)) " +
            "|| execution(* modu.menu.repository.PlaceRepository.saveAll(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // 실제 메서드 호출

        long end = System.currentTimeMillis();
        log.info("{} 실행 시간(ms): {}", joinPoint.getSignature(), end - start);

        return result;
    }
}
