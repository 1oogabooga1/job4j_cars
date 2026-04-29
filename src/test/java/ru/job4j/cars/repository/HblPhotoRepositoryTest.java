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
import ru.job4j.cars.model.Photo;

import java.util.Map;

class HblPhotoRepositoryTest {
    private static StandardServiceRegistry registry;

    private static SessionFactory sessionFactory;

    private static CrudRepository crudRepository;

    private static HblPhotoRepository photoRepository;

    @BeforeAll
    static void init() {
        registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        crudRepository = new CrudRepository(sessionFactory);
        photoRepository = new HblPhotoRepository(crudRepository);
    }

    @AfterAll
    static void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @BeforeEach
    void cleaTable() {
        crudRepository.run("DELETE FROM Photo", Map.of());
    }

    @Test
    void whenSaveThenFindByIdPhoto() {
        Photo photo = new Photo();
        photo.setPath("path");
        photo.setName("name");

        photoRepository.save(photo);
        var actual = photoRepository.findById(photo.getId()).get();
        assertThat(actual).isEqualTo(photo);
        assertThat(actual.getPath()).isEqualTo(photo.getPath());
        assertThat(actual.getName()).isEqualTo(photo.getName());
    }

    @Test
    void whenDeleteThenFindByIdIsEmpty() {
        Photo photo = new Photo();
        photo.setPath("path");
        photo.setName("name");

        photoRepository.save(photo);
        assertThat(photoRepository.findById(photo.getId()).get()).isEqualTo(photo);

        photoRepository.delete(photo.getId());

        assertThat(photoRepository.findById(photo.getId())).isEmpty();
    }
}