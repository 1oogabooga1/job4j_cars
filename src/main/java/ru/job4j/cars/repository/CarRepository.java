package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository {
    Car save(Car car);

    List<Car> getAll();

    Optional<Car> getById(int id);

}
