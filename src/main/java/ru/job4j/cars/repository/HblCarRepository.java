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
        return crudRepository.query("SELECT DISTINCT c FROM Car c LEFT JOIN FETCH c.owners", Car.class);
    }

    @Override
    public Optional<Car> getById(int id) {
        return crudRepository.optional("SELECT DISTINCT c From Car c LEFT JOIN FETCH c.owners WHERE c.id = :id",
                Car.class,
                Map.of("id", id));
    }
}
