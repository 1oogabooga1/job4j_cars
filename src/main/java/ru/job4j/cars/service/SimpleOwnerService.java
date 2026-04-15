package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.OwnerRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleOwnerService implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    public Optional<Owner> getById(int id) {
        return ownerRepository.getById(id);
    }
}
