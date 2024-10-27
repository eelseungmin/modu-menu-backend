package modu.menu.controller;

import io.swagger.v3.oas.annotations.Hidden;
import modu.menu.core.exception.Exception500;
import modu.menu.core.response.ErrorMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class SlackErrorLogTestController {

    @GetMapping("/api/slack")
    public String getSlackErrorLog() {
        throw new Exception500(ErrorMessage.ERROR_LOG_TEST_MESSAGE);
    }
}
