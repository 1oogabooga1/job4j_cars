package ru.job4j.cars.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.results.DeletePostResult;
import ru.job4j.cars.results.EditPostResult;
import ru.job4j.cars.results.MarkAsSoldPostResult;
import ru.job4j.cars.service.BrandService;
import ru.job4j.cars.service.EngineService;
import ru.job4j.cars.service.PostService;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    private PostService postService;

    private BrandService brandService;

    private EngineService engineService;

    private PostController postController;

    private MultipartFile testFile;

    @BeforeEach
    void init() {
        postService = mock(PostService.class);
        brandService = mock(BrandService.class);
        engineService = mock(EngineService.class);
        testFile = new MockMultipartFile("file", "test.png", "image/png", new byte[]{1, 2, 3});
        postController = new PostController(postService, brandService, engineService);
    }

    @Test
    void whenFindAllPostsBrandNameIsNullThenFindAllPosts() {
        Brand firstBrand = new Brand();
        firstBrand.setName("BMW");
        Brand secondBrand = new Brand();
        secondBrand.setName("Mazda");

        List<Brand> brands = List.of(firstBrand, secondBrand);
        List<Post> posts = List.of(new Post(), new Post());

        when(postService.findAllPosts(5)).thenReturn(posts);
        when(brandService.getAll()).thenReturn(brands);

        var model = new ConcurrentModel();
        var view = postController.getAllPosts(model, null, 5);

        assertThat(view).isEqualTo("posts/allPosts");
        assertThat(model.getAttribute("allPosts")).isEqualTo(posts);
        assertThat(model.getAttribute("brands")).isEqualTo(brands);
        assertThat(model.getAttribute("selectedBrand")).isNull();
        assertThat(model.getAttribute("activePage")).isEqualTo("allPosts");
        assertThat(model.getAttribute("selectedLimit")).isEqualTo(5);
        assertThat(model.getAttribute("limits")).isEqualTo(List.of(1, 5, 10, 15, 20));

        verify(postService).findAllPosts(5);
        verify(postService, never()).findPostsWithSpecialCarBrand(anyString(), anyInt());
    }

    @Test
    void whenGetAllPostsBrandNameIsBMWThenFindByBrand() {
        Brand bmw = new Brand();
        bmw.setName("BMW");

        Car car = new Car();
        car.setBrand(bmw);

        Post post = new Post();
        post.setCar(car);

        when(postService.findPostsWithSpecialCarBrand("BMW", 5)).thenReturn(List.of(post));
        when(brandService.getAll()).thenReturn(List.of(bmw));

        var model = new ConcurrentModel();
        var view = postController.getAllPosts(model, "BMW", 5);

        assertThat(view).isEqualTo("posts/allPosts");
        assertThat(model.getAttribute("allPosts")).isEqualTo(List.of(post));
        assertThat(model.getAttribute("selectedBrand")).isEqualTo("BMW");
        assertThat(model.getAttribute("activePage")).isEqualTo("allPosts");
        assertThat(model.getAttribute("selectedLimit")).isEqualTo(5);

        verify(postService).findPostsWithSpecialCarBrand("BMW", 5);
        verify(postService, never()).findAllPosts(anyInt());
    }

    @Test
    void whenLimitIsNotAvailableThenSelectedLimitIsDefault() {
        when(postService.findAllPosts(999)).thenReturn(List.of());
        when(brandService.getAll()).thenReturn(List.of());

        var model = new ConcurrentModel();
        var view = postController.getAllPosts(model, null, 999);

        assertThat(view).isEqualTo("posts/allPosts");
        assertThat(model.getAttribute("selectedLimit")).isEqualTo(5);
    }

    @Test
    void whenGetPostsForTheLastDayThenSuccess() {
        List<Post> posts = List.of(new Post());

        when(postService.findPostsForTheLastDay(10)).thenReturn(posts);

        var model = new ConcurrentModel();
        var view = postController.getPostsForTheLastDay(model, 10);

        assertThat(view).isEqualTo("posts/lastDayPosts");
        assertThat(model.getAttribute("postsForTheLastDay")).isEqualTo(posts);
        assertThat(model.getAttribute("activePage")).isEqualTo("lastDayPosts");
        assertThat(model.getAttribute("selectedLimit")).isEqualTo(10);

        verify(postService).findPostsForTheLastDay(10);
    }

    @Test
    void whenGetPostsWithPhotoThenSuccess() {
        List<Post> posts = List.of(new Post());

        when(postService.findPostsWithPhoto(15)).thenReturn(posts);

        var model = new ConcurrentModel();
        var view = postController.getPostsWithPhoto(model, 15);

        assertThat(view).isEqualTo("posts/photoPosts");
        assertThat(model.getAttribute("postsWithPhoto")).isEqualTo(posts);
        assertThat(model.getAttribute("activePage")).isEqualTo("photoPosts");
        assertThat(model.getAttribute("selectedLimit")).isEqualTo(15);

        verify(postService).findPostsWithPhoto(15);
    }

    @Test
    void whenCreationPageThenSuccess() {
        Brand brand = new Brand();
        when(brandService.getAll()).thenReturn(List.of(brand));
        when(engineService.getAll()).thenReturn(List.of());

        var model = new ConcurrentModel();
        var view = postController.creationPage(model);

        assertThat(view).isEqualTo("posts/create");
        assertThat(model.getAttribute("brands")).isEqualTo(List.of(brand));
        assertThat(model.getAttribute("engines")).isEqualTo(List.of());
    }

    @Test
    void whenCreatePostThenSuccess() throws IOException {
        Post post = new Post();
        User user = new User();
        user.setTimeZone("UTC-07");

        var postCaptor = ArgumentCaptor.forClass(Post.class);
        var photoCaptor = ArgumentCaptor.forClass(PhotoDto.class);

        var model = new ConcurrentModel();
        var view = postController.createPost(post, testFile, user, model);

        verify(postService).create(postCaptor.capture(), photoCaptor.capture());

        var actualPost = postCaptor.getValue();
        var actualPhoto = photoCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
        assertThat(actualPost.getUser()).isEqualTo(user);
        assertThat(actualPost.getCreated()).isNotNull();
        assertThat(actualPost.getCreated().atZone(ZoneId.of(user.getTimeZone())).getZone())
                .isEqualTo(ZoneId.of(user.getTimeZone()));
        assertThat(actualPhoto.getName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(actualPhoto.getContent()).isEqualTo(testFile.getBytes());
    }

    @Test
    void whenCreatePostWithBadPhotoThenReturn400() throws IOException {
        Post post = new Post();
        User user = new User();
        user.setTimeZone("UTC");

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.getOriginalFilename()).thenReturn("bad.png");
        when(badFile.getBytes()).thenThrow(new IOException());

        var model = new ConcurrentModel();
        var view = postController.createPost(post, badFile, user, model);

        assertThat(view).isEqualTo("errors/400");
        assertThat(model.getAttribute("message")).isEqualTo("Could not read uploaded photo");

        verify(postService, never()).create(any(Post.class), any(PhotoDto.class));
    }

    @Test
    void whenDeletePostThenSuccess() {
        User user = new User();
        user.setId(1);

        when(postService.delete(1, user.getId())).thenReturn(DeletePostResult.SUCCESS);

        var model = new ConcurrentModel();
        var view = postController.delete(1, user, model);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenDeleteNonExistingPostThenReturn404() {
        User user = new User();
        user.setId(1);

        when(postService.delete(1, user.getId())).thenReturn(DeletePostResult.NOT_FOUND);

        var model = new ConcurrentModel();
        var view = postController.delete(1, user, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("The post was not found");
    }

    @Test
    void whenSomeUserDeletesPostThenReturn403() {
        User user = new User();
        user.setId(2);

        when(postService.delete(1, user.getId())).thenReturn(DeletePostResult.FORBIDDEN);

        var model = new ConcurrentModel();
        var view = postController.delete(1, user, model);

        assertThat(view).isEqualTo("errors/403");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, only owner can delete this post");
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
        assertThat(model.getAttribute("brands")).isEqualTo(List.of());
        assertThat(model.getAttribute("engines")).isEqualTo(List.of());
    }

    @Test
    void whenPostDoesNotExistThenGetPostUnsuccessful() {
        when(postService.findById(1)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = postController.getPost(1, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("The post does not exist");
    }

    @Test
    void whenOwnerEditPostThenSuccess() {
        Post post = new Post();
        post.setId(1);

        User user = new User();
        user.setId(1);

        when(postService.edit(any(Post.class), any(PhotoDto.class), eq(user.getId())))
                .thenReturn(EditPostResult.SUCCESS);

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, user, model);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenEditNonExistingPostThenReturn404() {
        Post post = new Post();
        post.setId(1);

        User user = new User();
        user.setId(1);

        when(postService.edit(any(Post.class), any(PhotoDto.class), eq(user.getId())))
                .thenReturn(EditPostResult.NOT_FOUND);

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, user, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("The post was not found");
    }

    @Test
    void whenSomeUserEditsPostThenReturn403() {
        Post post = new Post();
        post.setId(1);

        User someUser = new User();
        someUser.setId(2);

        when(postService.edit(any(Post.class), any(PhotoDto.class), eq(someUser.getId())))
                .thenReturn(EditPostResult.FORBIDDEN);

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, someUser, model);

        assertThat(view).isEqualTo("errors/403");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, only owner can edit this post");
    }

    @Test
    void whenEditPostWithInvalidDataThenReturn400() {
        Post post = new Post();
        post.setId(1);

        User user = new User();
        user.setId(1);

        when(postService.edit(any(Post.class), any(PhotoDto.class), eq(user.getId())))
                .thenReturn(EditPostResult.INVALID_DATA);

        var model = new ConcurrentModel();
        var view = postController.editPost(post, testFile, user, model);

        assertThat(view).isEqualTo("errors/400");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, the data you want to change is invalid");
    }

    @Test
    void whenEditPostWithBadPhotoThenReturn400() throws IOException {
        Post post = new Post();
        post.setId(1);

        User user = new User();
        user.setId(1);

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.getOriginalFilename()).thenReturn("bad.png");
        when(badFile.getBytes()).thenThrow(new IOException());

        var model = new ConcurrentModel();
        var view = postController.editPost(post, badFile, user, model);

        assertThat(view).isEqualTo("errors/400");
        assertThat(model.getAttribute("message")).isEqualTo("Could not read uploaded photo");

        verify(postService, never()).edit(any(Post.class), any(PhotoDto.class), anyInt());
    }

    @Test
    void whenOwnerMarksPostAsSoldThenSuccess() {
        User owner = new User();
        owner.setId(1);

        when(postService.markAsSold(1, owner.getId())).thenReturn(MarkAsSoldPostResult.SUCCESS);

        var model = new ConcurrentModel();
        var view = postController.markAsSold(1, owner, model);

        assertThat(view).isEqualTo("redirect:/posts/allPosts");
    }

    @Test
    void whenMarkNonExistingPostAsSoldThenReturn404() {
        User user = new User();
        user.setId(1);

        when(postService.markAsSold(1, user.getId())).thenReturn(MarkAsSoldPostResult.NOT_FOUND);

        var model = new ConcurrentModel();
        var view = postController.markAsSold(1, user, model);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("The post was not found");
    }

    @Test
    void whenSomeUserMarksPostAsSoldThenReturn403() {
        User someUser = new User();
        someUser.setId(2);

        when(postService.markAsSold(1, someUser.getId())).thenReturn(MarkAsSoldPostResult.FORBIDDEN);

        var model = new ConcurrentModel();
        var view = postController.markAsSold(1, someUser, model);

        assertThat(view).isEqualTo("errors/403");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, only owner can mark as sold this post");
    }

    @Test
    void whenAlreadySoldPostMarksAsSoldThenReturn400() {
        User owner = new User();
        owner.setId(1);

        when(postService.markAsSold(1, owner.getId())).thenReturn(MarkAsSoldPostResult.ALREADY_SOLD);

        var model = new ConcurrentModel();
        var view = postController.markAsSold(1, owner, model);

        assertThat(view).isEqualTo("errors/400");
        assertThat(model.getAttribute("message")).isEqualTo("Sorry, this post is already marked as sold");
    }
}