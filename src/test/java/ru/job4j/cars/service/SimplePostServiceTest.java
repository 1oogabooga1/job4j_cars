package ru.job4j.cars.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.HblPostRepository;
import ru.job4j.cars.repository.PostRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimplePostServiceTest {
    private static PostRepository postRepository;

    private static PhotoService photoService;

    private static PostService postService;

    private static CarService carService;

    @BeforeAll
    static void init() {
        postRepository = mock(HblPostRepository.class);
        photoService = mock(PhotoService.class);
        carService = mock(SimpleCarService.class);
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
    void whenOwnerEditsPostThenGetNewPhoto() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);
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

        postService.edit(post, photoDto, 1);

        assertThat(post.getPhoto()).isEqualTo(newPhoto);
    }

    @Test
    void whenOwnerEditsPostWithoutNewPhotoThenPhotoIsOld() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        postService.edit(post, photoDto, 1);

        assertThat(post.getPhoto()).isEqualTo(oldPhoto);
    }

    @Test
    void whenSomeUserEditsPostThenException() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        var exception = assertThrows(IllegalArgumentException.class, () -> postService.edit(post, photoDto, 2));
        assertThat(exception.getMessage()).isEqualTo("Only owner can perform this action");
    }

    @Test
    void whenSomeUserSellsTheCarThenException() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setUser(user);
        post.setId(1);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        var exception = assertThrows(IllegalArgumentException.class,
                () -> postService.sellCar(post.getId(), 2));
        assertThat(exception.getMessage()).isEqualTo("Only owner can perform this action");
    }

    @Test
    void whenOwnerEditsNonExistingPostThenException() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);
        PhotoDto photoDto = new PhotoDto("name", new byte[]{});
        when(postRepository.findById(1)).thenReturn(Optional.empty());
        var exception = assertThrows(IllegalArgumentException.class,
                () -> postService.edit(post, photoDto, 1));
        assertThat(exception.getMessage()).isEqualTo("Post with id 1 was not found");
    }
}