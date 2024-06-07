package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.controller.Controller;
import ru.practicum.service.HitService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = Controller.class)
public class ControllerTest {
    @MockBean
    private HitService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final HitDto hit = new HitDto(1L, "app", "aaa",
            "180.000.0.0", "2024-08-06 00:00:00");

    @Test
    @SneakyThrows
    void send_shouldSendData() {
        when(service.post(any())).thenReturn(hit);
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(hit))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hit.getId()), Long.class))
                .andExpect(jsonPath("$.app", is(hit.getApp())))
                .andExpect(jsonPath("$.uri", is(hit.getUri())))
                .andExpect(jsonPath("$.ip", is(hit.getIp())))
                .andExpect(jsonPath("$.timestamp", is(hit.getTimestamp())));
    }

    @Test
    @SneakyThrows
    void shouldReturnListOfData() {
        when(service.getAll(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of(new ViewStatsDto(1L, "service", "qqq")));
        mvc.perform(get("/stats?start=2018-01-01 00:00:00&end=2025-01-01 00:00:00")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].app", is("service")))
                .andExpect(jsonPath("$.[0].uri", is("qqq")))
                .andExpect(jsonPath("$.[0].hits", is(1)));
    }
}
