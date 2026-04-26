package ru.job4j.cars.repository;

import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {

    List<Brand> getAll();

    Optional<Brand> getById(int id);
}
