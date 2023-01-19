package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
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
import java.util.List;

@Builder
@Getter
@Setter
public class ItemDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    @Min(value = 0, message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String name;

    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String description;

    @BooleanFlag
    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    private Boolean available;

    @JsonView({Details.class, AdminDetails.class})
    private ItemOwner owner;

    @JsonView({Details.class, AdminDetails.class})
    private ItemBooking lastBooking;

    @JsonView({Details.class, AdminDetails.class})
    private ItemBooking nextBooking;

    @JsonView({Details.class, AdminDetails.class})
    private List<CommentDto> comments;

    @Builder
    @Getter
    @Setter
    public static class ItemOwner {
        private final long id;
        private final String name;
    }

    @Data
    public static class ItemBooking {
        private final long id;
        private final long bookerId;
    }
}