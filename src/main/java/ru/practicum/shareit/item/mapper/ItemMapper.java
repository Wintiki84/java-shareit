package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(ItemDto.ItemOwner.builder()
                        .id(item.getOwner().getId())
                        .name(item.getOwner().getName())
                        .build())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
