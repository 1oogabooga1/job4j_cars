package ru.job4j.cars.service;

import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

public interface EngineService {

    List<Engine> getAll();

    Optional<Engine> getById(int id);
}
