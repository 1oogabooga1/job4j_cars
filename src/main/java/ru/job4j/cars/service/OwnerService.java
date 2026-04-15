package ru.job4j.cars.service;

import ru.job4j.cars.model.Owner;

import java.util.Optional;

public interface OwnerService {

    Optional<Owner> getById(int id);
}
