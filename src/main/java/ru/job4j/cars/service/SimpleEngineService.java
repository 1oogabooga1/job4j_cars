package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.EngineRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleEngineService implements EngineService {

    private final EngineRepository engineRepository;

    @Override
    public List<Engine> getAll() {
        return engineRepository.getAll();
    }

    @Override
    public Optional<Engine> getById(int id) {
        return engineRepository.getById(id);
    }
}
