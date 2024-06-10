package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.HitDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatsClient {

    private static final String BASE_URL = "http://localhost:9090";
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";

    private final RestTemplate rest;

    @Autowired
    public StatsClient(RestTemplateBuilder builder) {
        this.rest =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    public void addHit(HitDto hitDto) {
        post(hitDto);
    }

    public ResponseEntity<Object> retrieveAllStats(String start, String end, List<String> uris,
                                                   boolean unique) {
        String urisListParam = String.join("&uris=", uris);
        String path = String.format("%s?start=%s&end=%s&uris=%s&unique=%s",
                STATS_ENDPOINT, start, end, urisListParam, unique);
        return get(path);
    }

    private ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null);
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
