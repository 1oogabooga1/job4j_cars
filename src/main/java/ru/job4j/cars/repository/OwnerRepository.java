package ru.job4j.cars.repository;

import ru.job4j.cars.model.Owner;

import java.util.Optional;

public interface OwnerRepository {

    Optional<Owner> getById(int id);
}
