package modu.menu.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class HealthCheckController {

    @GetMapping("/api/health-check")
    public String getHealthCheck() {
        return "up";
    }

}
