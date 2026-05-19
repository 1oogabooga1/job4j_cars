package ru.job4j.cars.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.List;

class HblPostRepositoryTest {
    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static HblPostRepository postRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        postRepository = new HblPostRepository(sessionFactory);
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @BeforeEach
    void clean() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Post").executeUpdate();
            session.createQuery("DELETE FROM Car").executeUpdate();
            session.createQuery("DELETE FROM Brand").executeUpdate();
            session.createQuery("DELETE FROM Engine").executeUpdate();
            session.createQuery("DELETE FROM Photo").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenShowPostForTheLastDayLimit3ThenNoMoreThan3Posts() {
        Post first = new Post();
        first.setCreated(LocalDateTime.now());
        Post second = new Post();
        second.setCreated(LocalDateTime.now());
        Post third = new Post();
        third.setCreated(LocalDateTime.now());
        Post fourth = new Post();
        fourth.setCreated(LocalDateTime.now());
        postRepository.create(first);
        postRepository.create(second);
        postRepository.create(third);
        postRepository.create(fourth);
        assertThat(postRepository.findPostsForTheLastDay(3)).hasSize(3);
        assertThat(postRepository.findPostsForTheLastDay(5)).hasSize(4);
        assertThat(postRepository.findPostsForTheLastDay(4)).hasSize(4);
        assertThat(postRepository.findPostsForTheLastDay(3).get(0).getCreated().getDayOfMonth())
                .isEqualTo(first.getCreated().getDayOfMonth());
    }

    @Test
    void whenPostWithPhoto() {
        Photo photo = new Photo();
        photo.setName("first photo");
        photo.setPath("somePath");
        save(photo);

        Post post = new Post();
        post.setPhoto(photo);
        post.setCreated(LocalDateTime.now());
        post.setDescription("description");
        postRepository.create(post);

        Post secondPost = new Post();
        secondPost.setDescription("desc");
        secondPost.setCreated(LocalDateTime.now());
        postRepository.create(secondPost);

        assertThat(postRepository.findPostsWithPhoto(5)).hasSize(1);
        assertThat(postRepository.findPostsWithPhoto(5)).hasSize(1);
        assertThat(postRepository.findPostsWithPhoto(1).get(0).getPhoto()).isEqualTo(photo);
        assertThat(postRepository.findPostsForTheLastDay(5)).hasSize(2);
        assertThat(postRepository.findPostsForTheLastDay(2)).containsExactlyInAnyOrder(post, secondPost);
    }

    @Test
    void whenPostsWithSpecialCarModels() {
        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);
        Brand secondBrand = new Brand();
        secondBrand.setName("Mercedes");
        save(secondBrand);

        Car car = new Car();
        car.setBrand(brand);
        save(car);
        Car secondCar = new Car();
        secondCar.setBrand(secondBrand);
        save(secondCar);

        Post post = new Post();
        post.setCar(car);
        post.setCreated(LocalDateTime.now());
        post.setDescription("description");
        save(post);

        Post secondPost = new Post();
        secondPost.setCar(secondCar);
        secondPost.setDescription("desc");
        secondPost.setCreated(LocalDateTime.now());
        save(secondPost);

        assertThat(postRepository.findPostsWithSpecialCarBrand("BMW", 5).get(0).getCar().getBrand().getName()).isEqualTo("BMW");
        assertThat(postRepository.findPostsWithSpecialCarBrand("Mercedes", 5)).hasSize(1);
        assertThat(postRepository.findPostsWithSpecialCarBrand(null, 5)).isEqualTo(List.of());
        assertThat(postRepository.findAllPosts(5)).hasSize(2);
    }

    @Test
    void whenDeleteIsTrueThenFindByIdIsEmpty() {
        Post post = new Post();
        postRepository.create(post);

        assertThat(postRepository.findById(post.getId())).isNotEmpty();

        var result = postRepository.delete(post.getId());

        assertThat(result).isTrue();
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    void whenEditPostThenGetUpdatedInfo() {
        Photo photo = new Photo();
        photo.setName("old");
        photo.setPath("somePath");
        save(photo);
        Photo newPhoto = new Photo();
        newPhoto.setName("new");
        newPhoto.setPath("some path");
        save(newPhoto);
        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);
        Brand newBrand = new Brand();
        newBrand.setName("new brand");
        save(newBrand);
        Car car = new Car();
        car.setBrand(brand);
        save(car);
        Car newCar = new Car();
        newCar.setBrand(newBrand);
        save(newCar);
        Post post = new Post();
        post.setCar(car);
        post.setPhoto(photo);
        post.setDescription("Old description");
        postRepository.create(post);
        assertThat(postRepository.findById(post.getId()).get().getDescription()).isEqualTo("Old description");
        post.setDescription("New description");
        post.setCar(newCar);
        post.setPhoto(newPhoto);
        postRepository.edit(post);
        Post newPost = postRepository.findById(post.getId()).get();
        assertThat(newPost.getDescription()).isEqualTo("New description");
        assertThat(newPost.getCar()).isEqualTo(newCar);
        assertThat(newPost.getCar().getBrand()).isEqualTo(newBrand);
        assertThat(newPost.getPhoto().getName()).isEqualTo("new");
    }

    @Test
    void whenSellCarIsTrueThenCarIsSold() {
        Post post = new Post();
        postRepository.create(post);
        assertThat(post.isSold()).isFalse();

        var result = postRepository.markAsSold(post.getId());

        var postFromDb = postRepository.findById(post.getId()).get();
        assertThat(result).isTrue();
        assertThat(postFromDb.isSold()).isTrue();
    }

    private <T> void save(T model) {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(model);
            session.getTransaction().commit();
        }
    }
}