package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void bookingDtoTest() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(dateTime.plusDays(1))
                .end(dateTime.plusDays(10))
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(dateTime.plusDays(1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(dateTime.plusDays(10).toString());
    }
}
