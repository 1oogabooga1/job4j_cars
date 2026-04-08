package ru.job4j.cars.repository;

import ru.job4j.cars.model.Engine;

import java.util.Optional;

public interface EngineRepository {
     Optional<Engine> getById(int id);
}
