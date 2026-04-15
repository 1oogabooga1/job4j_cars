package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post create(Post post) {
        return postRepository.create(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    @Override
    public List<Post> showPostsForTheLastDay() {
        return postRepository.showPostsForTheLastDay();
    }

    @Override
    public List<Post> postsWithPhoto() {
        return postRepository.postsWithPhoto();
    }

    @Override
    public List<Post> postsWithSpecialCarModel(String model) {
        return postRepository.postsWithSpecialCarModel(model);
    }
}
