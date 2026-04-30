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
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;

class HblCarRepositoryTest {
    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static CrudRepository crudRepository;

    private static HblCarRepository carRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        carRepository = new HblCarRepository(crudRepository);
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @BeforeEach
    void clearTables() {
        crudRepository.run("DELETE FROM Car", Map.of());
        crudRepository.run("DELETE FROM Engine", Map.of());
        crudRepository.run("DELETE FROM Brand", Map.of());
    }

    @Test
    void whenGetAllThenReturnAllCars() {
        Engine engine = new Engine();
        save(engine);

        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);

        Car first = new Car();
        first.setEngine(engine);
        first.setBrand(brand);
        save(first);

        Car second = new Car();
        second.setEngine(engine);
        second.setBrand(brand);
        carRepository.save(second);

        assertThat(carRepository.getAll())
                .usingRecursiveComparison().isEqualTo(List.of(first, second));
        assertThat(carRepository.getAll()).hasSize(2);
    }

    @Test
    void whenGetByIdThenObtainTheCar() {
        Engine engine = new Engine();
        save(engine);

        Brand brand = new Brand();
        brand.setName("BMW");
        save(brand);

        Car car = new Car();
        car.setEngine(engine);
        car.setBrand(brand);
        carRepository.save(car);

        assertThat(carRepository.getById(car.getId()).get()).isEqualTo(car);
        assertThat(carRepository.getById(0)).isEmpty();

    }

    @Test
    void whenNoCarsSavedThenGetAllIsEmpty() {
        assertThat(carRepository.getAll()).isEmpty();
        assertThat(carRepository.getAll()).hasSize(0);
    }

    private <T> void save(T entity) {
        crudRepository.run(session -> session.save(entity));
    }
}