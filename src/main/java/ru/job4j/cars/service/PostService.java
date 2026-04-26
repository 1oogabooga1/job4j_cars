package ru.job4j.cars.service;

import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post create(Post post, PhotoDto photoDto);

    void delete(int id);

    void edit(Post postFromSession, PhotoDto photo);

    void sellCar(int id);

    Optional<Post> findById(int id);

    List<Post> getAllPosts();

    List<Post> showPostsForTheLastDay();

    List<Post> postsWithPhoto();

    List<Post> postsWithSpecialCarModel(String model);
}
