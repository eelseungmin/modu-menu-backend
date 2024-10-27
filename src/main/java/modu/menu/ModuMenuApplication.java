package modu.menu;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default server url")})
@SpringBootApplication
public class ModuMenuApplication {

    public static void main(String[] args) {
//        try {
//            SpringApplication.run(ModuMenuApplication.class, args);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        SpringApplication.run(ModuMenuApplication.class, args);
    }

}
