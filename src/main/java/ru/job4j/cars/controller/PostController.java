package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.BrandService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.PostService;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    private final BrandService brandService;

    private final EngineService engineService;

    private static final List<Integer> AVAILABLE_LIMITS = List.of(1, 5, 10, 15, 20);

    private static final int DEFAULT_LIMIT = 5;

    @GetMapping("/allPosts")
    public String getAllPosts(Model model,
                              @RequestParam(required = false) String brandName,
                              @RequestParam(defaultValue = "5") int limit) {
        var posts = brandName == null || brandName.isBlank()
                ? postService.findAllPosts(limit)
                : postService.findPostsWithSpecialCarBrand(brandName, limit);
        model.addAttribute("allPosts", posts);
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("selectedBrand", brandName);
        model.addAttribute("activePage", "allPosts");
        addLimit(model, normalizeSelectedLimit(limit));
        return "posts/allPosts";
    }

    @GetMapping("/lastDayPosts")
    public String getPostsForTheLastDay(Model model,
                                        @RequestParam(defaultValue = "5") int limit) {
        model.addAttribute("postsForTheLastDay", postService.findPostsForTheLastDay(limit));
        model.addAttribute("activePage", "lastDayPosts");
        addLimit(model, normalizeSelectedLimit(limit));
        return "posts/lastDayPosts";
    }

    @GetMapping("/photoPosts")
    public String getPostsWithPhoto(Model model,
                                    @RequestParam(defaultValue = "5") int limit) {
        model.addAttribute("postsWithPhoto", postService.findPostsWithPhoto(limit));
        model.addAttribute("activePage", "photoPosts");
        addLimit(model, normalizeSelectedLimit(limit));
        return "posts/photoPosts";
    }

    @GetMapping("/create")
    public String creationPage(Model model) {
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("engines", engineService.getAll());
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post,
                             @RequestParam MultipartFile file,
                             @SessionAttribute("user") User user,
                             Model model) {
        try {
            post.setCreated(ZonedDateTime.now().withZoneSameInstant(ZoneId.of(user.getTimeZone())).toLocalDateTime());
            post.setUser(user);
            postService.create(post, new PhotoDto(file.getOriginalFilename(), file.getBytes()));
            return "redirect:/posts/allPosts";
        } catch (IOException e) {
            model.addAttribute("message", "Could not read uploaded photo");
            return "errors/400";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, @SessionAttribute User user, Model model) {
        return switch (postService.delete(id, user.getId())) {
            case NOT_FOUND -> {
                model.addAttribute("message", "The post was not found");
                yield  "errors/404";
            }
            case FORBIDDEN -> {
                model.addAttribute("message", "Sorry, only owner can delete this post");
                yield  "errors/403";
            }
            case SUCCESS -> "redirect:/posts/allPosts";
        };
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Integer id, Model model) {
        var post = postService.findById(id);
        if (post.isEmpty()) {
            model.addAttribute("message", "The post does not exist");
            return "errors/404";
        }
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("engines", engineService.getAll());
        model.addAttribute("post", post.get());
        return "posts/post";
    }

    @PostMapping("/edit")
    public String editPost(@ModelAttribute Post post,
                           @RequestParam MultipartFile file,
                           @SessionAttribute User user,
                           Model model) {
        try {
            return switch (postService.edit(post, new PhotoDto(file.getOriginalFilename(), file.getBytes()), user.getId())) {
                case NOT_FOUND -> {
                    model.addAttribute("message", "The post was not found");
                    yield "errors/404";
                }
                case FORBIDDEN -> {
                    model.addAttribute("message", "Sorry, only owner can edit this post");
                    yield "errors/403";
                }
                case INVALID_DATA -> {
                    model.addAttribute("message", "Sorry, the data you want to change is invalid");
                    yield  "errors/400";
                }
                case SUCCESS -> "redirect:/posts/allPosts";
            };
        } catch (IOException e) {
            model.addAttribute("message", "Could not read uploaded photo");
            return "errors/400";
        }
    }

    @PostMapping("/sell/{id}")
    public String markAsSold(@PathVariable int id,
                             @SessionAttribute User user,
                             Model model) {
        return switch (postService.markAsSold(id, user.getId())) {
            case NOT_FOUND -> {
                model.addAttribute("message", "The post was not found");
                yield "errors/404";
            }
            case FORBIDDEN -> {
                model.addAttribute("message", "Sorry, only owner can mark as sold this post");
                yield "errors/403";
            }
            case ALREADY_SOLD -> {
                model.addAttribute("message", "Sorry, this post is already marked as sold");
                yield "errors/400";
            }
            case SUCCESS -> "redirect:/posts/allPosts";
        };
    }

    private void addLimit(Model model, int selectedLimit) {
        model.addAttribute("selectedLimit", selectedLimit);
        model.addAttribute("limits", AVAILABLE_LIMITS);
    }

    private int normalizeSelectedLimit(int limit) {
        return AVAILABLE_LIMITS.contains(limit) ? limit : DEFAULT_LIMIT;
    }
}
