package ru.job4j.cars.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}
