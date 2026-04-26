package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post create(Post post);

    void delete(int id);

    void edit(Post post);

    Optional<Post> findById(int id);

    void sellCar(int id);

    List<Post> getAllPosts();

    List<Post> showPostsForTheLastDay();

    List<Post> postsWithPhoto();

    List<Post> postsWithSpecialCarModel(String carModel);
}
