package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class HblOwnerRepository implements OwnerRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Owner> getById(int id) {
        return crudRepository.optional("FROM Owner WHERE id = :id",
                Owner.class,
                Map.of(":id", id));
    }
}
