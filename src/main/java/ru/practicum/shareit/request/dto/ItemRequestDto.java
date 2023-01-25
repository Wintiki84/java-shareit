package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validator.AdminDetails;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Details;
import ru.practicum.shareit.validator.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    @Positive(message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class, Update.class}, message = "Не должно быть пустым")
    private String description;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private LocalDateTime created;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private List<ItemDto> items;
}
