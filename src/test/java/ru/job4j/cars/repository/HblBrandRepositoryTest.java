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
import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Map;

class HblBrandRepositoryTest {
    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static CrudRepository crudRepository;

    private static HblBrandRepository brandRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        brandRepository = new HblBrandRepository(crudRepository);
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @BeforeEach
    void clean() {
        crudRepository.run("DELETE FROM Brand", Map.of());
    }

    @Test
    void whenGetAll() {
        Brand first = new Brand();
        first.setName("First");
        save(first);

        Brand second = new Brand();
        second.setName("Second");
        save(second);

        Brand third = new Brand();
        third.setName("Third");
        save(third);

        var actual = brandRepository.getAll();

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(first, second, third));
        assertThat(actual).hasSize(3);
    }

    @Test
    void whenGetById() {
        Brand first = new Brand();
        first.setName("First");
        save(first);

        assertThat(brandRepository.getById(first.getId()).get()).isEqualTo(first);
        assertThat(brandRepository.getById(0)).isEmpty();
    }

    private <T> void save(T entity) {
        crudRepository.run(session -> session.save(entity));
    }
}