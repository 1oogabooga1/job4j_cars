package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.PostRepository;
import ru.job4j.cars.results.EditPostResult;
import ru.job4j.cars.results.MarkAsSoldPostResult;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SimplePostServiceTest {
    private static PostRepository postRepository;

    private static PhotoService photoService;

    private static PostService postService;

    private static CarService carService;

    @BeforeEach
    void init() {
        postRepository = mock(PostRepository.class);
        photoService = mock(PhotoService.class);
        carService = mock(CarService.class);
        postService = new SimplePostService(postRepository, photoService, carService);
    }

    @Test
    void whenCreateThenFindById() {
        Brand brand = new Brand();
        brand.setName("BMW");
        Car car = new Car();
        car.setBrand(brand);

        Post post = new Post();
        post.setCar(car);

        Car carWithId = new Car();
        carWithId.setId(10);
        carWithId.setBrand(brand);
        PhotoDto photoDto = new PhotoDto("dto", new byte[]{1, 2, 3});

        Photo photo = new Photo();
        when(photoService.save(photoDto)).thenReturn(photo);
        when(carService.save(car)).thenReturn(carWithId);
        when(postRepository.create(post)).thenReturn(post);
        postService.create(post, photoDto);

        assertThat(post.getPhoto()).isEqualTo(photo);
        assertThat(post.getCar().getBrand().getName()).isEqualTo("BMW");
        assertThat(post.getCar().getId()).isEqualTo(10);

    }

    @Test
    void whenEditPostResultIsSuccessThenGetNewPhoto() {
        Car car = new Car();
        car.setBrand(new Brand());
        car.setEngine(new Engine());
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        post.setCar(car);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{1, 2, 3});

        Photo newPhoto = new Photo();
        newPhoto.setPath("path");
        newPhoto.setName("name");
        when(photoService.save(photoDto)).thenReturn(newPhoto);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        var result = postService.edit(post, photoDto, 1);

        assertThat(result).isEqualTo(EditPostResult.SUCCESS);
        assertThat(post.getPhoto()).isEqualTo(newPhoto);
    }

    @Test
    void whenEditPostWithoutFullCarThenResultIsInvalidData() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        var result = postService.edit(post, photoDto, 1);
        assertThat(result).isEqualTo(EditPostResult.INVALID_DATA);
    }

    @Test
    void whenSomeUserEditsPostThenResultIsForbidden() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        var result = postService.edit(post, photoDto, 2);

        assertThat(result).isEqualTo(EditPostResult.FORBIDDEN);
    }

    @Test
    void whenEditNonExistingPostThenResultIsNotFound() {
        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        var result = postService.edit(new Post(), photoDto, 2);

        assertThat(result).isEqualTo(EditPostResult.NOT_FOUND);
    }

    @Test
    void whenOwnerMarksPostAsSoldThenResultIsSuccess() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setUser(user);
        post.setId(1);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.markAsSold(1)).thenReturn(true);

        var result = postService.markAsSold(post.getId(), 1);
        assertThat(result).isEqualTo(MarkAsSoldPostResult.SUCCESS);
    }

    @Test
    void whenSomeUserMarksPostAsSoldThenResultIsForbidden() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setUser(user);
        post.setId(1);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        var result = postService.markAsSold(post.getId(), 2);
        assertThat(result).isEqualTo(MarkAsSoldPostResult.FORBIDDEN);
    }

    @Test
    void whenMarkNonExistingPostAsSoldThenResultIsNotFound() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());
        var result = postService.markAsSold(1, 1);
        assertThat(result).isEqualTo(MarkAsSoldPostResult.NOT_FOUND);
    }

    @Test
    void whenMarkSoldPostAsSoldThenResultIsAlreadySold() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setUser(user);
        post.setId(1);
        post.setSold(true);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        var result = postService.markAsSold(post.getId(), 1);
        assertThat(result).isEqualTo(MarkAsSoldPostResult.ALREADY_SOLD);
    }

    @Test
    void whenMarkAsSoldRepositoryReturnsFalseThenResultIsNotFound() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setUser(user);
        post.setId(1);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postRepository.markAsSold(1)).thenReturn(false);

        var result = postService.markAsSold(post.getId(), 1);

        assertThat(result).isEqualTo(MarkAsSoldPostResult.NOT_FOUND);
    }

    @Test
    void whenFindWithSpecialCarBrandBrandIsNullThenListIsEmpty() {
        var result = postService.findPostsWithSpecialCarBrand(null, 5);
        assertThat(result).isEmpty();
        verify(postRepository, never()).findPostsWithSpecialCarBrand(anyString(), anyInt());
    }

    @Test
    void whenFindAllPostsWithNegativeLimitThenRepositoryGetsDefaultLimit() {
        postService.findAllPosts(-1);
        verify(postRepository).findAllPosts(5);
    }

    @Test
    void whenFindAllPostsWithZeroLimitThenRepositoryGetsDefaultLimit() {
        postService.findAllPosts(0);
        verify(postRepository).findAllPosts(5);
    }

    @Test
    void whenFindAllPostsWithTooBigLimitThenRepositoryGetsMaxLimit() {
        postService.findAllPosts(100);
        verify(postRepository).findAllPosts(20);
    }

    @Test
    void whenFindAllPostsWithNormalLimitThenRepositoryGetsSameLimit() {
        postService.findAllPosts(10);
        verify(postRepository).findAllPosts(10);
    }

    @Test
    void whenFindPostsForTheLastDayWithAbnormalLimitThenRepositoryGetsDefaultLimit() {
        postService.findPostsForTheLastDay(-10);
        verify(postRepository).findPostsForTheLastDay(5);
    }

    @Test
    void whenFindPostsForTheLastDayWithTooBigLimitThenRepositoryGetsMaxLimit() {
        postService.findPostsForTheLastDay(100);
        verify(postRepository).findPostsForTheLastDay(20);
    }

    @Test
    void whenFindPostsWithPhotoWithAbnormalLimitThenRepositoryGetsDefaultLimit() {
        postService.findPostsWithPhoto(0);
        verify(postRepository).findPostsWithPhoto(5);
    }

    @Test
    void whenFindPostsWithPhotoWithTooBigLimitThenRepositoryGetsMaxLimit() {
        postService.findPostsWithPhoto(100);
        verify(postRepository).findPostsWithPhoto(20);
    }

    @Test
    void whenFindPostsWithSpecialCarBrandWithAbnormalLimitThenRepositoryGetsDefaultLimit() {
        postService.findPostsWithSpecialCarBrand("BMW", -1);
        verify(postRepository).findPostsWithSpecialCarBrand("BMW", 5);
    }

    @Test
    void whenFindPostsWithSpecialCarBrandWithTooBigLimitThenRepositoryGetsMaxLimit() {
        postService.findPostsWithSpecialCarBrand("BMW", 100);
        verify(postRepository).findPostsWithSpecialCarBrand("BMW", 20);
    }
}