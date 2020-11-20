package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class RegisterPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    @Json
    private void register(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = new User(request.getParameter("login"), request.getParameter("email"));
        String password = request.getParameter("password");
        String passwordConfirmation = request.getParameter("passwordConfirmation");

        userService.validateRegistration(user, password, passwordConfirmation);
        userService.register(user, password);

        request.getSession().setAttribute("message", "You are successfully registered!");
        throw new RedirectException("/index");
    }
}
