package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;
import ru.job4j.cars.results.DeletePostResult;
import ru.job4j.cars.results.EditPostResult;
import ru.job4j.cars.results.MarkAsSoldPostResult;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {

    private final PostRepository postRepository;

    private final PhotoService photoService;

    private final CarService carService;

    private static final int MAX_POST_LIMIT = 20;

    private static final int DEFAULT_POST_LIMIT = 5;

    @Override
    public Post create(Post post, PhotoDto photoDto) {
        var photo = photoService.save(photoDto);
        var car = carService.save(post.getCar());
        post.setCar(car);
        post.setPhoto(photo);
        return postRepository.create(post);
    }

    @Override
    public DeletePostResult delete(int id, int userId) {
        var postOptional = findById(id);
        if (postOptional.isEmpty()) {
            return DeletePostResult.NOT_FOUND;
        }
        var post = postOptional.get();
        if (!isOwner(post, userId)) {
            return DeletePostResult.FORBIDDEN;
        }
        boolean deleteRsl = postRepository.delete(id);
        if (!deleteRsl) {
            return DeletePostResult.NOT_FOUND;
        }
        if (post.getPhoto() != null) {
            int photoId = post.getPhoto().getId();
            photoService.delete(photoId);
        }
        return DeletePostResult.SUCCESS;
    }

    @Override
    public EditPostResult edit(Post postFromSession, PhotoDto photo, int userId) {
        var isNewFileEmpty = photo.getContent() == null || photo.getContent().length == 0;
        var postOptional = findById(postFromSession.getId());
        if (postOptional.isEmpty()) {
            return EditPostResult.NOT_FOUND;
        }
        var post = postOptional.get();
        if (!isOwner(post, userId)) {
            return EditPostResult.FORBIDDEN;
        }
        if (postFromSession.getCar() == null
                || postFromSession.getCar().getBrand() == null
                || postFromSession.getCar().getEngine() == null) {
            return EditPostResult.INVALID_DATA;
        }
        post.setDescription(postFromSession.getDescription());
        post.getCar().setBrand(postFromSession.getCar().getBrand());
        post.getCar().setEngine(postFromSession.getCar().getEngine());
        if (isNewFileEmpty) {
            postRepository.edit(post);
        } else {
            var oldPhoto = post.getPhoto();
            var newPhoto = photoService.save(photo);
            post.setPhoto(newPhoto);
            postRepository.edit(post);
            if (oldPhoto != null) {
                photoService.delete(oldPhoto.getId());
            }
        }
        return EditPostResult.SUCCESS;
    }

    @Override
    public MarkAsSoldPostResult markAsSold(int id, int userId) {
        var postOptional = findById(id);
        if (postOptional.isEmpty()) {
            return MarkAsSoldPostResult.NOT_FOUND;
        }
        var post = postOptional.get();
        if (!isOwner(post, userId)) {
            return MarkAsSoldPostResult.FORBIDDEN;
        }
        if (post.isSold()) {
            return MarkAsSoldPostResult.ALREADY_SOLD;
        }
        var markRsl = postRepository.markAsSold(id);
        if (!markRsl) {
            return MarkAsSoldPostResult.NOT_FOUND;
        }
        return MarkAsSoldPostResult.SUCCESS;
    }

    @Override
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findAllPosts(int limit) {
        return postRepository.findAllPosts(normalizeLimit(limit));
    }

    @Override
    public List<Post> findPostsForTheLastDay(int limit) {
        return postRepository.findPostsForTheLastDay(normalizeLimit(limit));
    }

    @Override
    public List<Post> findPostsWithPhoto(int limit) {
        return postRepository.findPostsWithPhoto(normalizeLimit(limit));
    }

    @Override
    public List<Post> findPostsWithSpecialCarBrand(String brand, int limit) {
        if (brand == null || brand.isBlank()) {
            return List.of();
        }
        return postRepository.findPostsWithSpecialCarBrand(brand, normalizeLimit(limit));
    }

    private boolean isOwner(Post post, int userId) {
        return post.getUser() != null
                && Integer.valueOf(userId).equals(post.getUser().getId());
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            limit = DEFAULT_POST_LIMIT;
        }
        return Math.min(limit, MAX_POST_LIMIT);
    }
}
