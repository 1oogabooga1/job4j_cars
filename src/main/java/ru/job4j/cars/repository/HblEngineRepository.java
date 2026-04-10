package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class HblEngineRepository implements EngineRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Engine> getById(int id) {
        return crudRepository.optional("From Engine WHERE id = :id",
                Engine.class,
                Map.of("id", id));
    }
}
