package ru.job4j.cars.repository;

import ru.job4j.cars.model.Photo;

import java.util.Optional;

public interface PhotoRepository {
    Photo save(Photo photo);

    Optional<Photo> findById(int id);

    void delete(int id);
}
