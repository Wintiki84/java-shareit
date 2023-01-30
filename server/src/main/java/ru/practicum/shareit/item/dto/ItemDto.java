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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.util.List;

@Builder
@Getter
@Setter
public class ItemDto {

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class}, message = "Должно быть пустым")
    @Positive(groups = {Update.class}, message = "Должно быть больше нуля")
    private Long id;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String name;

    @JsonView({Details.class, AdminDetails.class})
    @NotBlank(groups = {Create.class}, message = "Не должно быть пустым")
    private String description;

    @BooleanFlag
    @JsonView({Details.class, AdminDetails.class})
    @NotNull(groups = {Create.class}, message = "Не должно быть пустым")
    private Boolean available;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private ItemOwner owner;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private ItemBooking lastBooking;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private ItemBooking nextBooking;

    @JsonView({Details.class, AdminDetails.class})
    @Null(groups = {Create.class, Update.class}, message = "Должно быть пустым")
    private List<CommentDto> comments;

    @JsonView({Details.class, AdminDetails.class})
    private Long requestId;

    @Builder
    @Getter
    @Setter
    public static class ItemOwner {
        @JsonView({Details.class, AdminDetails.class})
        private final long id;
        @JsonView({Details.class, AdminDetails.class})
        private final String name;
    }

    @Data
    public static class ItemBooking {
        @JsonView({Details.class, AdminDetails.class})
        private final long id;
        @JsonView({Details.class, AdminDetails.class})
        private final long bookerId;
    }
}