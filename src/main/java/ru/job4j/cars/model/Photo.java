package ru.job4j.cars.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String path;
}
