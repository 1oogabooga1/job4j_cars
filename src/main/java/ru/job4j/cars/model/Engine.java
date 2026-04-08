package ru.job4j.cars.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "engine")
public class Engine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
