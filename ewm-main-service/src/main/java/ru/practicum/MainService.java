package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import ru.practicum.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@Slf4j
public class MainService {

    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }

    @Bean
    public CommandLineRunner runner(ApplicationContext appContext) {
        return args -> {
            StatsClient statsClient = appContext.getBean(StatsClient.class);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime past = now.minusDays(6);
            LocalDateTime future = now.plusDays(12);

            log.info("Testing hit saving...");
            HitDto hitDto = createHitDto("aaa", "/test1", "100.0.0.1", now);
            statsClient.addHit(hitDto);

            log.info("Retrieving statistics for the past hour...");
            ResponseEntity<Object> response = statsClient.retrieveAllStats(past, future, List.of("/test1"), true);
            log.info("Statistics response: {}", response);

            log.info("Testing second hit saving...");
            HitDto hitDto2 = createHitDto("aaa2", "/test2", "100.0.0.1", now);
            statsClient.addHit(hitDto2);

            log.info("Retrieving non-unique hits statistics...");
            response = statsClient.retrieveAllStats(past, future, List.of("/test1", "/test2"), false);
            log.info("Non-unique statistics response: {}", response);
        };
    }

    private HitDto createHitDto(String appName, String uri, String ip, LocalDateTime timestamp) {
        return new HitDto(appName, uri, ip, timestamp.toString());
    }
}
