package ru.job4j.cars.service;

import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.results.DeletePostResult;
import ru.job4j.cars.results.EditPostResult;
import ru.job4j.cars.results.MarkAsSoldPostResult;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post create(Post post, PhotoDto photoDto);

    DeletePostResult delete(int id, int userId);

    EditPostResult edit(Post postFromSession, PhotoDto photo, int userId);

    MarkAsSoldPostResult markAsSold(int id, int userId);

    Optional<Post> findById(int id);

    List<Post> findAllPosts(int limit);

    List<Post> findPostsForTheLastDay(int limit);

    List<Post> findPostsWithPhoto(int limit);

    List<Post> findPostsWithSpecialCarBrand(String brand, int limit);
}
