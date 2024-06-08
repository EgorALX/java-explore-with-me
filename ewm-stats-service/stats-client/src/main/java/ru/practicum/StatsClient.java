package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
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
        return restTemplate.postForEntity(path, requestEntity, responseType);
    }

    protected <T> ResponseEntity<T> exchangeGet(String path, Map<String, Object> uriVariables, Class<T> responseType) {
        return restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(null, defaultHeaders()), responseType, uriVariables);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public HitDto post(HitDto hitDto) {
        return exchangePost(API_PREFIX + "hits", hitDto, HitDto.class).getBody();
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
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
    }
}
