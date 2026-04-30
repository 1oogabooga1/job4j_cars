package ru.job4j.cars.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;

import java.util.List;

class HblEngineRepositoryTest {

    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static CrudRepository crudRepository;

    private static HblEngineRepository engineRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        engineRepository = new HblEngineRepository(crudRepository);
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @BeforeEach
    void clearTables() {
        crudRepository.run("DELETE FROM Engine", java.util.Map.of());
    }

    @Test
    void whenFindByIdThenGetEngine() {
        Engine engine = new Engine();
        save(engine);
        assertThat(engineRepository.getById(engine.getId()).get()).isEqualTo(engine);
        assertThat(engineRepository.getById(2)).isEmpty();
    }

    @Test
    void whenSaveTwoEnginesThenGetAllTwoEngines() {
        Engine first = new Engine();
        save(first);

        Engine second = new Engine();
        save(second);

        assertThat(engineRepository.getAll()).usingRecursiveComparison().isEqualTo(List.of(first, second));
    }

    private <T> void save(T entity) {
        crudRepository.tx(session -> session.save(entity));
    }
}