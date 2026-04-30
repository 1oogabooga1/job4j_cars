package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhotoDto {
    @EqualsAndHashCode.Include
    private String name;

    private byte[] content;
}
