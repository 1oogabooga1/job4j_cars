package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HblPhotoRepository implements PhotoRepository {

    private final CrudRepository crudRepository;

    @Override
    public Photo save(Photo photo) {
        crudRepository.run(session -> session.save(photo));
        return photo;
    }

    @Override
    public Optional<Photo> findById(int id) {
        return crudRepository.optional("FROM Photo WHERE id = :id", Photo.class, Map.of("id", id));
    }

    @Override
    public void delete(int id) {
        crudRepository.run("DELETE FROM Photo WHERE id = :id", Map.of("id", id));
    }
}
