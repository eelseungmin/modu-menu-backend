package modu.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import modu.menu.core.auth.jwt.JwtProvider;
import modu.menu.core.config.WebMvcConfig;
import modu.menu.controller.PlaceController;
import modu.menu.service.PlaceService;
import modu.menu.controller.ReviewController;
import modu.menu.service.ReviewService;
import modu.menu.controller.UserController;
import modu.menu.repository.UserRepository;
import modu.menu.service.UserService;
import modu.menu.controller.VoteController;
import modu.menu.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        UserController.class,
        VoteController.class,
        ReviewController.class,
        PlaceController.class
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
})
public abstract class ControllerTestSupporter {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JwtProvider jwtProvider;

    @MockBean
    protected UserService userService;

    @MockBean
    protected ReviewService reviewService;

    @MockBean
    protected PlaceService placeService;

    @MockBean
    protected VoteService voteService;

    @MockBean
    protected UserRepository userRepository;
}
