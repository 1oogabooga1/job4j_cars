package ru.job4j.cars.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

class PostControllerTest {
    private static PostService postService;

    private static CarService carService;

    private static BrandService brandService;

    private static EngineService engineService;

    private static PostController postController;

    private static MultipartFile testFile;

    @BeforeAll
    static void init() {
        postService = mock(SimplePostService.class);
        brandService = mock(SimpleBrandService.class);
        engineService = mock(SimpleEngineService.class);
        carService = mock(SimpleCarService.class);
        testFile = new MockMultipartFile("test", new byte[]{1, 2, 3});
        postController = new PostController(postService, carService, brandService, engineService);
    }

    @Test
    void whenGetAllPostsBrandNameIsNullThenGetAllPosts() {
        Brand firstBrand = new Brand();
        firstBrand.setName("BMW");
        Brand secondBrand = new Brand();
        secondBrand.setName("Mazda");

        Car firstCar = new Car();
        firstCar.setBrand(firstBrand);
        Car secondCar = new Car();
        secondCar.setBrand(secondBrand);

        Post firstPost = new Post();
        firstPost.setCar(firstCar);
        Post secondPost = new Post();
        secondPost.setCar(secondCar);

        List<Post> posts = List.of(firstPost, secondPost);
        List<Brand> brands = List.of(firstBrand, secondBrand);

        when(postService.getAllPosts()).thenReturn(posts);
        when(brandService.getAll()).thenReturn(brands);

        var model = new ConcurrentModel();
        var view = postController.getAllPosts(model, null);
        var actualPosts = model.getAttribute("allPosts");

        assertThat(posts).usingRecursiveComparison().isEqualTo(actualPosts);
        assertThat(view).isEqualTo("posts/allPosts");
        assertThat(model.getAttribute("selectedBrand")).isNull();

    }

    @Test
    void whenGetAllPostsBrandNameIsBMWThenGetOnlyBMW() {
        Brand firstBrand = new Brand();
        firstBrand.setName("BMW");
        Brand secondBrand = new Brand();
        secondBrand.setName("Mazda");

        Car firstCar = new Car();
        firstCar.setBrand(firstBrand);
        Car secondCar = new Car();
        secondCar.setBrand(secondBrand);

        Post firstPost = new Post();
        firstPost.setCar(firstCar);
        Post secondPost = new Post();
        secondPost.setCar(secondCar);

        List<Post> posts = List.of(firstPost, secondPost);
        List<Brand> brands = List.of(firstBrand, secondBrand);

        when(postService.postsWithSpecialCarModel(firstBrand.getName())).thenReturn(List.of(firstPost));
        when(brandService.getAll()).thenReturn(brands);

        var model = new ConcurrentModel();
        var view = postController.getAllPosts(model, "BMW");
        var actualPosts = model.getAttribute("allPosts");

        assertThat(actualPosts).usingRecursiveComparison().isEqualTo(List.of(firstPost));
        assertThat(view).isEqualTo("posts/allPosts");
        assertThat(model.getAttribute("selectedBrand")).isEqualTo("BMW");
    }

    @Test
    void whenCreatePostThenSuccess() throws IOException {
        Post post = new Post();
        User user = new User();
        user.setTimeZone("UTC-07");
        PhotoDto photoDto = new PhotoDto(testFile.getOriginalFilename(), testFile.getBytes());

        var postCaptor = ArgumentCaptor.forClass(Post.class);
        var photoCaptor = ArgumentCaptor.forClass(PhotoDto.class);

        when(postService.create(postCaptor.capture(), photoCaptor.capture())).thenReturn(post);

        var model = new ConcurrentModel();
        var view = postController.createPost(post, testFile, user, model);

        var actualPost = postCaptor.getValue();
        var actualPhoto = photoCaptor.getValue();

        assertThat(actualPost).usingRecursiveComparison().isEqualTo(post);
        assertThat(actualPhoto).usingRecursiveComparison().isEqualTo(photoDto);
        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenSomeUserEditPostThenException() {
        Post post = new Post();
        post.setId(1);
        User user = new User();
        user.setId(1);
        post.setUser(user);
        User someUser = new User();
        someUser.setId(2);

        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, someUser, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, only owner can edit the post.");
    }

    @Test
    void whenOwnerEditPostThenSuccess() {
        Post post = new Post();
        post.setId(1);
        User user = new User();
        user.setId(1);
        post.setUser(user);

        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, user, model);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenSomeUserSellsCarThenException() {
        Post post = new Post();
        post.setId(1);
        User owner = new User();
        owner.setId(1);
        post.setUser(owner);
        User someUser = new User();
        someUser.setId(3);
        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.sellCar(post.getId(), someUser, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message"))
                .isEqualTo("Sorry, only owner can sell the car.");
    }

    @Test
    void whenOwnerSellsTheCarThenSuccess() {
        Post post = new Post();
        post.setId(1);
        User owner = new User();
        owner.setId(1);
        post.setUser(owner);

        when(postService.findById(post.getId())).thenReturn(Optional.of(post));

        var model = new ConcurrentModel();
        var view = postController.sellCar(post.getId(), owner, model);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenGetPostThenSuccess() {
        Post post = new Post();
        post.setId(1);

        when(postService.findById(post.getId())).thenReturn(Optional.of(post));
        when(brandService.getAll()).thenReturn(List.of());
        when(engineService.getAll()).thenReturn(List.of());
        var model = new ConcurrentModel();
        var view = postController.getPost(post.getId(), model);

        assertThat(view).isEqualTo("posts/post");
        assertThat(model.getAttribute("post")).isEqualTo(post);
    }

    @Test
    void whenPostDoesNotExistsThenGetPostUnsuccessful() {
        Post post = new Post();
        post.setId(1);

        when(postService.findById(post.getId())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.getPost(post.getId(), model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("The post does not exist");
    }
}