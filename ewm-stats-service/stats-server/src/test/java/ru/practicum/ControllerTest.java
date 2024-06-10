package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.controller.HitController;
import ru.practicum.service.HitService;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = HitController.class)
public class ControllerTest {
    @MockBean
    private HitService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final HitDto hit = new HitDto(1L, "app", "aaa",
            "180.000.0.0", LocalDateTime.now());
    private final ViewStatsDto response = new ViewStatsDto(1L, "aaa", "/ccc");

    @Test
    @SneakyThrows
    void shouldCallAddHitWithCorrectParameters() throws Exception {
        mvc.perform(post("/hit")
                .content(mapper.writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        verify(service, times(1)).addHit(any());
    }

    @Test
    void getStats_whenCorrectParams_thenReturnStatsWithStatusOk() throws Exception {
        String eventStart = "2020-01-01 00:00:00";
        String eventEnd = "2030-01-01 00:00:00";
        List<String> uris = List.of("/fff");
        boolean unique = false;
        ViewStatsDto dto = new ViewStatsDto(1L, "app", "/fff");

        when(service.getAll(any(LocalDateTime.class), any(LocalDateTime.class), anyList(),
                anyBoolean())).thenReturn(List.of(dto));

        mvc.perform(get("/stats")
                        .param("start", eventStart)
                        .param("end", eventEnd)
                        .param("uris", String.valueOf(uris))
                        .param("unique", String.valueOf(unique))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app"))
                .andExpect(jsonPath("$[0].uri").value("/fff"))
                .andExpect(jsonPath("$[0].hits").value(1));
    }

    @Test
    void badRequestTest() throws Exception {
        String eventStart = "2010-01-01 01:01:01";
        String eventEnd = "2000-01-01 01:01:01";
        List<String> uris = List.of("/ccc");
        mvc.perform(get("/stats")
                        .param("start", eventStart)
                        .param("end", eventEnd)
                        .param("uris", String.join(",", uris))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
