package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
@Slf4j
public class MainService {

    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
        StatsClient client = new StatsClient("http://localhost:9090", new RestTemplateBuilder());
        LocalDateTime now = LocalDateTime.now();
        String stringNowPlusHour = now.minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String stringNowMinusHour = now.plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        HitDto hitDto = new HitDto("app", "/aaa", "sss", LocalDateTime.now());
        client.addHit(hitDto);
        List<ViewStatsDto> dtos = client.retrieveAllStats(stringNowPlusHour,
                stringNowMinusHour, List.of("/aaa"), true);
        System.out.println(dtos);

        HitDto hitDto2 = new HitDto("appp", "/bbb", "ssss", LocalDateTime.now().plusMinutes(3));
        client.addHit(hitDto2);

        dtos = client.retrieveAllStats(stringNowPlusHour,
                stringNowMinusHour, List.of("/aaa", "/bbb"), false);
        System.out.println(dtos);

    }
}