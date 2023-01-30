package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controllers.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.HEADER;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerMockTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void findAllTest() throws Exception {
        List<UserDto> users = List.of((buildUserDto(null)));

        Mockito.when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        Mockito.verify(userService, Mockito.times(1))
                .getAllUsers();
    }

    @Test
    void findByIdTest() throws Exception {
        UserDto userDto = buildUserDto(1L);
        Mockito.when(userService.getById(Mockito.anyLong()))
                .thenReturn(userDto);
        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));
    }

    @Test
    void saveTest() throws Exception {
        UserDto userDto = buildUserDto(null);

        Mockito.when(userService.save(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        Mockito.verify(userService, Mockito.times(1))
                .save(Mockito.any(UserDto.class));
    }

    @Test
    void updateTest() throws Exception {
        UserDto userDto = buildUserDto(null);

        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        Mockito.verify(userService, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.any(UserDto.class));
    }

    private UserDto buildUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .name("test")
                .email("test@mail.com")
                .build();
    }
}
