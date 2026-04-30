package ru.job4j.cars.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.HblPostRepository;
import ru.job4j.cars.repository.PostRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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
    void whenEditPostThenGetNewPhoto() {
        Post post = new Post();
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{1, 2, 3});

        Photo newPhoto = new Photo();
        newPhoto.setPath("path");
        newPhoto.setName("name");
        when(photoService.save(photoDto)).thenReturn(newPhoto);

        postService.edit(post, photoDto);

        assertThat(post.getPhoto()).isEqualTo(newPhoto);
    }

    @Test
    void whenEditPostWithoutNewPhotoThenPhotoIsOld() {
        Post post = new Post();
        Photo oldPhoto = new Photo();
        oldPhoto.setId(10);
        oldPhoto.setName("old");
        post.setPhoto(oldPhoto);

        PhotoDto photoDto = new PhotoDto("name", new byte[]{});

        postService.edit(post, photoDto);

        assertThat(post.getPhoto()).isEqualTo(oldPhoto);
    }
}