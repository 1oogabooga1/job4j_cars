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

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
            session.createQuery("DELETE FROM User").executeUpdate();
            session.createQuery("DELETE FROM Photo").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenShowPostForTheLastDay() {
        Photo photo = new Photo();
        photo.setName("first photo");
        photo.setPath("somePath");
        save(photo);

        Engine engine = new Engine();
        save(engine);

        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);

        Car car = new Car();
        car.setEngine(engine);
        car.setBrand(brand);
        save(car);

        User user = new User();
        user.setPassword("password");
        user.setLogin("login");
        save(user);

        Post post = new Post();
        post.setCar(car);
        post.setPhoto(photo);
        post.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        post.setDescription("description");
        save(post);

        assertThat(postRepository.showPostsForTheLastDay()).hasSize(1);
        assertThat(postRepository.showPostsForTheLastDay().get(0).getDescription()).isEqualTo("description");
        assertThat(postRepository.showPostsForTheLastDay().get(0).getCreated().getDay()).isEqualTo(post.getCreated().getDay());
        assertThat(postRepository.showPostsForTheLastDay().get(0).getPhoto()).isEqualTo(photo);
    }

    @Test
    void whenPostWithPhoto() {
        Photo photo = new Photo();
        photo.setName("first photo");
        photo.setPath("somePath");
        save(photo);

        Engine engine = new Engine();
        save(engine);
        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);

        Car car = new Car();
        car.setEngine(engine);
        car.setBrand(brand);
        save(car);

        User user = new User();
        user.setPassword("password");
        user.setLogin("login");
        save(user);

        Post post = new Post();
        post.setCar(car);
        post.setPhoto(photo);
        post.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        post.setDescription("description");
        save(post);
        Post secondPost = new Post();
        secondPost.setCar(car);
        secondPost.setDescription("desc");
        secondPost.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        save(secondPost);

        assertThat(postRepository.postsWithPhoto()).hasSize(1);
        assertThat(postRepository.postsWithPhoto().get(0).getPhoto()).isEqualTo(photo);
        assertThat(postRepository.showPostsForTheLastDay()).hasSize(2);
        assertThat(postRepository.showPostsForTheLastDay()).containsExactlyInAnyOrder(post, secondPost);
    }

    @Test
    void whenPostsWithSpecialCarModels() {
        Photo photo = new Photo();
        photo.setName("first photo");
        photo.setPath("somePath");
        save(photo);
        Engine engine = new Engine();
        save(engine);
        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);
        Brand secondBrand = new Brand();
        secondBrand.setName("Mercedes");
        save(secondBrand);
        Car car = new Car();
        car.setEngine(engine);
        car.setBrand(brand);
        save(car);
        Car secondCar = new Car();
        secondCar.setEngine(engine);
        secondCar.setBrand(secondBrand);
        save(secondCar);
        User user = new User();
        user.setPassword("password");
        user.setLogin("login");
        save(user);
        Post post = new Post();
        post.setCar(car);
        post.setPhoto(photo);
        post.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        post.setDescription("description");
        save(post);
        Post secondPost = new Post();
        secondPost.setCar(secondCar);
        secondPost.setDescription("desc");
        secondPost.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        save(secondPost);

        assertThat(postRepository.postsWithSpecialCarModel("BMW").get(0).getCar().getBrand().getName()).isEqualTo("BMW");
        assertThat(postRepository.postsWithSpecialCarModel("Mercedes")).hasSize(1);
    }

    private <T> void save(T model) {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(model);
            session.getTransaction().commit();
        }
    }
}