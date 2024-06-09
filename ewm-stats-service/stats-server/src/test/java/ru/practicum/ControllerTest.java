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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = HitController.class)
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
    void shouldCallAddHitWithCorrectParameters() throws Exception {
        mvc.perform(post("/hit")
                .content(new ObjectMapper().writeValueAsString(hit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        verify(service, times(1)).addHit(any());
    }

    @Test
    void getStats_whenCorrectParams_thenReturnStatsWithStatusOk() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(6);
        LocalDateTime future = now.plusDays(12);
        List<String> uris = List.of("/hhhh");
        boolean unique = false;

        ViewStatsDto dto = new ViewStatsDto();
        dto.setApp("aaa");
        dto.setUri("/hhhh");
        dto.setHits(1L);

        when(service.getAll(past, future, uris, unique))
                .thenReturn(List.of(dto));

        mvc.perform(get("/stats")
                        .param("start", past.toString())
                        .param("end", future.toString())
                        .param("uris", String.valueOf(uris))
                        .param("unique", String.valueOf(unique))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
