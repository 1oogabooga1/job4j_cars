package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post create(Post post);

    boolean delete(int id);

    void edit(Post post);

    Optional<Post> findById(int id);

    boolean markAsSold(int id);

    List<Post> findAllPosts(int limit);

    List<Post> findPostsForTheLastDay(int limit);

    List<Post> findPostsWithPhoto(int limit);

    List<Post> findPostsWithSpecialCarBrand(String brand, int limit);
}
