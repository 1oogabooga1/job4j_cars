package ru.job4j.cars.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;

import java.util.List;

class UserRepositoryTest {

    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static CrudRepository crudRepository;

    private static UserRepository userRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        userRepository = new UserRepository(crudRepository);
    }

    @AfterEach
    void clean() {
        for (User user : userRepository.findAllOrderById()) {
            userRepository.delete(user.getId());
        }
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Test
    void whenGetUsers() {
        User first = new User();
        first.setLogin("login");
        first.setPassword("password");
        userRepository.create(first);

        User second = new User();
        second.setLogin("login_2");
        second.setPassword("password");
        userRepository.create(second);

        List<User> expected = List.of(first, second);

        assertThat(userRepository.findAllOrderById()).usingRecursiveComparison().isEqualTo(expected);
        assertThat(userRepository.findByLikeLogin("login")).isEqualTo(expected);
        assertThat(userRepository.findByLogin("login").get()).isEqualTo(first);
        assertThat(userRepository.findByLogin("login_2").get()).isEqualTo(second);
        assertThat(userRepository.findById(first.getId()).get()).isEqualTo(first);
        assertThat(userRepository.findById(second.getId()).get()).isEqualTo(second);

    }

    @Test
    void whenUpdateUsers() {
        User second = new User();
        second.setLogin("login_2");
        second.setPassword("password");
        userRepository.create(second);

        second.setPassword("new_password");
        userRepository.update(second);

        assertThat(userRepository.findByLogin("login_2").get().getPassword()).isEqualTo("new_password");
    }

    @Test
    void whenDeleteUsers() {
        User first = new User();
        first.setLogin("login");
        first.setPassword("password");
        userRepository.create(first);

        userRepository.delete(first.getId());

        assertThat(userRepository.findByLogin("login")).isEmpty();
    }
}