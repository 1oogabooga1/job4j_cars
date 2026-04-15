package ru.job4j.cars.service;

import ru.job4j.cars.model.Post;

import java.util.List;

public interface PostService {
    Post create(Post post);

    List<Post> getAllPosts();

    List<Post> showPostsForTheLastDay();

    List<Post> postsWithPhoto();

    List<Post> postsWithSpecialCarModel(String model);
}
