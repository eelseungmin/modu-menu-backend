package modu.menu;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default server url")})
@SpringBootApplication
public class ModuMenuApplication {

    public static void main(String[] args) {
//        try {
//            SpringApplication.run(ModuMenuApplication.class, args);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
        SpringApplication.run(ModuMenuApplication.class, args);
    }

}
