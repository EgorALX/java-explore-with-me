package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.util.*;

@Slf4j
@Component
public class StatsClient implements Client {

    private static final String HIT_ENDPOINT = "/hit";

    private static final String STATS_ENDPOINT = "/stats";

    private final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${statistic-server.url}") String baseUrl, RestTemplateBuilder builder) {
        this.rest =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    public void addHit(HitDto hitDto) {
        HttpEntity<HitDto> requestEntity = new HttpEntity<>(hitDto, defaultHeaders());
        try {
            ResponseEntity<Object> response = rest.exchange(HIT_ENDPOINT, HttpMethod.POST, requestEntity, Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Hit added successfully");
            } else {
                log.error("Failed to add hit, status code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error during adding hit: {}", e.getMessage());
        }
    }

    public List<ViewStatsDto> retrieveAllStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath(STATS_ENDPOINT)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        try {
            ResponseEntity<List<ViewStatsDto>> response = rest.exchange(
                    uriComponentsBuilder.build().toString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully retrieved stats");
                return response.getBody();
            } else {
                log.error("HTTP request failed with status code: {}", response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error: message:{}", e.getMessage());
            return List.of();
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
