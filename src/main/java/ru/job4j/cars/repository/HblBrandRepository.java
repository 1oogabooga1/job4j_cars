package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HblBrandRepository implements BrandRepository {

    private final CrudRepository crudRepository;

    @Override
    public List<Brand> getAll() {
        return crudRepository.query("FROM Brand", Brand.class);
    }

    @Override
    public Optional<Brand> getById(int id) {
        return crudRepository.optional("FROM Brand WHERE id = :id", Brand.class, Map.of("id", id));
    }
}
