package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;

    public Map<String, Object> toMap() {
        return Map.of(
                "name", name
        );
    }
}
