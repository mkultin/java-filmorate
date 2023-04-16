package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private Integer useful;

    public Map<String, Object> toMap() {
        return Map.of(
                "content", content,
                "is_positive", isPositive,
                "user_id", userId,
                "film_id", filmId
        );
    }
}
