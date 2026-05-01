package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {

    private final PostRepository postRepository;

    private final PhotoService photoService;

    private final CarService carService;

    @Override
    public Post create(Post post, PhotoDto photoDto) {
        var photo = photoService.save(photoDto);
        var car = carService.save(post.getCar());
        post.setCar(car);
        post.setPhoto(photo);
        return postRepository.create(post);
    }

    @Override
    public void delete(int id) {
        int photoId = postRepository.findById(id).get().getPhoto().getId();
        postRepository.delete(id);
        photoService.delete(photoId);
    }

    @Override
    public void edit(Post postFromSession, PhotoDto photo) {
        var isNewFileEmpty = photo.getContent().length == 0;
        if (isNewFileEmpty) {
            postRepository.edit(postFromSession);
        } else {
            var oldPhoto = postFromSession.getPhoto();
            var newPhoto = photoService.save(photo);
            postFromSession.setPhoto(newPhoto);
            postRepository.edit(postFromSession);
            photoService.delete(oldPhoto.getId());
        }
    }

    @Override
    public void sellCar(int id) {
        postRepository.sellCar(id);
    }

    @Override
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
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
