package ru.job4j.cars.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.SimpleUserService;
import ru.job4j.cars.service.UserService;

import java.util.Optional;

class UserControllerTest {
    private static UserService userService;

    private static UserController userController;

    @BeforeAll
    static void init() {
        userService = mock(SimpleUserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenEmailAndLoginAreCorrectThenSuccess() {
        User user = new User();
        when(userService.findByLoginAndPassword(user)).thenReturn(Optional.of(user));

        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.login(model, user, request);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenEmailOrPasswordAreIncorrectThenLoginFail() {
        User user = new User();
        when(userService.findByLoginAndPassword(user)).thenReturn(Optional.empty());

        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.login(model, user, request);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Invalid e-mail or password");
    }

    @Test
    void whenEmailIsUniqueThenRegisterSuccessful() {
        User user = new User();
        when(userService.create(user)).thenReturn(Optional.of(user));

        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.register(model, user, request);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenEmailRepeatsThenRegisterFail() {
        User user = new User();
        when(userService.create(user)).thenReturn(Optional.empty());

        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var model = new ConcurrentModel();
        var view = userController.register(model, user, request);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message"))
                .isEqualTo("User with the same e-mail already exists");
    }
}