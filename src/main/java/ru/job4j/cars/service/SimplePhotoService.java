package ru.job4j.cars.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PhotoDto;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.PhotoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class SimplePhotoService implements PhotoService {

    private final PhotoRepository photoRepository;

    private final String storageDirectory;

    public SimplePhotoService(PhotoRepository hblPhotoRepository,
                              @Value("${file.directory:files}") String storageDirectory) {
        this.photoRepository = hblPhotoRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Photo save(PhotoDto dto) {
        var path = getNewFilePath(dto.getName());
        writeFileBytes(path, dto.getContent());
        return photoRepository.save(new Photo(dto.getName(), path));
    }

    @Override
    public Optional<PhotoDto> getById(int id) {
        var optional = photoRepository.findById(id);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        var content = readFileAsBytes(optional.get().getPath());
        return Optional.of(new PhotoDto(optional.get().getName(), content));
    }

    @Override
    public void delete(int id) {
        var optional = photoRepository.findById(id);
        if (optional.isPresent()) {
            deleteFile(optional.get().getPath());
            photoRepository.delete(id);
        }
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory + java.io.File.separator + UUID.randomUUID() + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFileAsBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
