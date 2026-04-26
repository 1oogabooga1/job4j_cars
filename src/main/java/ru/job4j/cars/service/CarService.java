package ru.job4j.cars.service;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarService {

    Car save(Car car);

    List<Car> getAll();

    Optional<Car> getById(int id);
}
