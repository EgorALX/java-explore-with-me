package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.util.ArrayList;
import java.util.List;

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
        post(hitDto);
    }

    public List<ViewStatsDto> retrieveAllStats(String start, String end, List<String> uris, boolean unique) {
        String urisListParam = String.join("&uris=", uris);
        String path = String.format("%s?start=%s&end=%s&uris=%s&unique=%b",
                STATS_ENDPOINT, start, end, urisListParam, unique);
        return get(path);
    }

    private List<ViewStatsDto> get(String path) {
        ResponseEntity<Object> response = makeAndSendRequest(HttpMethod.GET, path, null);
        List<ViewStatsDto> dtos = (List<ViewStatsDto>) response.getBody();
        return dtos;
    }

    private <T> void post(T body) {
        makeAndSendRequest(HttpMethod.POST, HIT_ENDPOINT, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        try {
            ResponseEntity<Object> statServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            return prepareClientResponse(statServerResponse);
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Failed to send request to stats server", e);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareClientResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        return ResponseEntity.ok(new ArrayList<>());
    }
}
