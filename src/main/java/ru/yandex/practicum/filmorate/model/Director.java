package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
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
