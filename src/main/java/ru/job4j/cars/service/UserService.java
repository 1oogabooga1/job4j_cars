package ru.job4j.cars.service;

import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User create(User user);

    void update(User user);

    void delete(int id);

    List<User> findAllOrderById();

    Optional<User> findById(int id);

    List<User> findByLikeLogin(String key);

    Optional<User> findByLogin(String login);
}
