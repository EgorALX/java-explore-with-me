package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient implements Client {

    private final RestTemplate restTemplate;

    private static final String API_PREFIX = "/";

    @Autowired
    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected <T> ResponseEntity<T> exchangePost(String path, T body, Class<T> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<T> response = restTemplate.postForEntity(path, requestEntity, responseType);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to post data: " + response.getBody());
        }
        return response;
    }

    protected <T> ResponseEntity<T> exchangeGet(String path, Map<String, Object> uriVariables, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path)
                .queryParam("start", uriVariables.get("start"))
                .queryParam("end", uriVariables.get("end"))
                .queryParam("uris", uriVariables.get("uris"))
                .queryParam("unique", uriVariables.get("unique"));
        ResponseEntity<T> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(null, defaultHeaders()), responseType);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get data: " + response.getBody());
        }
        return response;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public void post(HitDto hitDto) {
        exchangePost(API_PREFIX + "hits", hitDto, HitDto.class).getBody();
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
            Map<String, Object> uriVariables = new HashMap<>();
            uriVariables.put("start", start.toLocalDate().toString());
            uriVariables.put("end", end.toLocalDate().toString());
            uriVariables.put("uris", uris);
            uriVariables.put("unique", unique);

            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(API_PREFIX + "stats")
                    .queryParam("start", "{start}")
                    .queryParam("end", "{end}")
                    .queryParam("uris", "{uris}")
                    .queryParam("unique", "{unique}");

            return exchangeGet(builder.toUriString(), uriVariables, List.class).getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
