package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDtoTest() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("testDescription")
                .created(dateTime)
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("testDescription");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(dateTime.toString());
    }
}
