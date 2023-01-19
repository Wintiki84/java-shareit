package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    @Min(value = 0, message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class, Update.class}, message = "Не должно быть пустым")
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    private String text;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private String authorName;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private LocalDateTime created;
}
