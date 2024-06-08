package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Client extends BaseClient {

    private static final String API_PREFIX = "/";

    @Autowired
    public Client(RestTemplate restTemplate) {
        super(restTemplate);
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
