package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {

    @EqualsAndHashCode.Exclude
    private Long id;

    @NotBlank(message = "Не должно быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
