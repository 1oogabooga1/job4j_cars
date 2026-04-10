package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class HblCarRepository implements CarRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Car> getAll() {
        return crudRepository.query("FROM Car", Car.class);
    }

    @Override
    public Optional<Car> getById(int id) {
        return crudRepository.optional("From Car WHERE id = :id",
                Car.class,
                Map.of("id", id));
    }
}
