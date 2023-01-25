package ru.practicum.shareit.constantsTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.constants.Constants.HEADER;

@JsonTest
public class Constants {

    @Test
    @DisplayName(" test for HEADER Constant")
    void equal() {
        assertEquals(HEADER, "X-Sharer-User-Id");
    }

}
