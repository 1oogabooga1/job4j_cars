package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cars.dto.TimeZoneDto;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Optional;
import java.util.TimeZone;

@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "users/login";
    }

    @PostMapping("/login")
    public String login(Model model, @ModelAttribute User user, HttpServletRequest request) {
        var getUser = userService.findByLoginAndPassword(user);
        if (getUser.isEmpty()) {
            model.addAttribute("message", "Invalid e-mail or password");
            return "errors/404";
        }
        request.getSession().setAttribute("user", getUser.get());
        return "redirect:/posts/allPosts";
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model) {
        var zones = new ArrayList<TimeZoneDto>();
        for (String timeId : TimeZone.getAvailableIDs()) {
            TimeZone zone = TimeZone.getTimeZone(timeId);
            zones.add(new TimeZoneDto(
                    zone.getID(),
                    zone.getID() + " (" + zone.getDisplayName(java.util.Locale.ENGLISH) + ")"
            ));
        }
        model.addAttribute("zones", zones);
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user, HttpServletRequest request) {
        Optional<User> userOpt = userService.create(user);
        if (userOpt.isEmpty()) {
            model.addAttribute("message", "User with the same e-mail already exists");
            return "errors/404";
        }
        request.getSession().setAttribute("user", userOpt.get());
        return "redirect:/posts/allPosts";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}

