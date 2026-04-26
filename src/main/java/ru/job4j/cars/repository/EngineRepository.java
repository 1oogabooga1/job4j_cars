package ru.job4j.cars.repository;

import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

public interface EngineRepository {

     List<Engine> getAll();

     Optional<Engine> getById(int id);
}
