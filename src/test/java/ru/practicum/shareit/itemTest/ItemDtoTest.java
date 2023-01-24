package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @Test
    void itemDtoTest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test")
                .description("testDescription")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("testDescription");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isTrue();
    }
}
