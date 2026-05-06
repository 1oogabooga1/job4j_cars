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
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Controller
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    private final BrandService brandService;

    private final EngineService engineService;

    @GetMapping("/allPosts")
    public String getAllPosts(Model model,
                              @RequestParam(required = false) String brandName) {
        var posts = brandName == null || brandName.isBlank()
                ? postService.getAllPosts()
                : postService.postsWithSpecialCarBrand(brandName);
        model.addAttribute("allPosts", posts);
        model.addAttribute("brands", brandService.getAll());
        model.addAttribute("selectedBrand", brandName);
        return "posts/allPosts";
    }

    @GetMapping("/lastDayPosts")
    public String getPostsForTheLastDay(Model model) {
        model.addAttribute("postsForTheLastDay", postService.showPostsForTheLastDay());
        return "posts/lastDayPosts";
    }

    @GetMapping("/photoPosts")
    public String getPostsWithPhoto(Model model) {
        model.addAttribute("postsWithPhoto", postService.postsWithPhoto());
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
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, @SessionAttribute User user, Model model) {
        try {
            postService.delete(id, user.getId());
            return "redirect:/posts/allPosts";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
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
            postService.edit(post, new PhotoDto(file.getOriginalFilename(), file.getBytes()), user.getId());
            return "redirect:/posts/allPosts";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/sell/{id}")
    public String sellCar(@PathVariable int id,
                          @SessionAttribute User user,
                          Model model) {
        try {
            postService.sellCar(id, user.getId());
            return "redirect:/posts/allPosts";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }
}
