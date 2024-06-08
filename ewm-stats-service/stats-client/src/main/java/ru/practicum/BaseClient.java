package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    private final RestTemplate restTemplate;

    @Autowired
    public BaseClient(RestTemplate restTemplate) {
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
}
