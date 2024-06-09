package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import ru.practicum.controller.HitController;
import ru.practicum.service.HitService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = HitController.class)
public class ControllerTest {
    @MockBean
    private HitService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final HitDto hit = new HitDto(1L, "app", "aaa",
            "180.000.0.0", "2024-08-06T00:00:00");
    private final ViewStatsDto response = new ViewStatsDto(1L, "aaa", "/ccc");

    @Test
    @SneakyThrows
    void shouldCallAddHitWithCorrectParameters() throws Exception {
        mvc.perform(post("/hit")
                .content(new ObjectMapper().writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        verify(service, times(1)).addHit(any());
    }

    @Test
    void badRequestTest() throws Exception {
        String eventStart = "3000-00-01T00:00:01";
        String eventEnd = "1000-00-00T00:00:01";
        List<String> uris = List.of("/ccc");
        mvc.perform(get("/stats")
                        .param("start", eventStart)
                        .param("end", eventEnd)
                        .param("uris", String.valueOf(uris))
                        .param("unique", String.valueOf(false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
