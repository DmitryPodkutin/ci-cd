package com.example.cicd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.cicd.component.DateTimeFormat.DATE_FORMATTER;
import static com.example.cicd.component.DateTimeFormat.TIME_FORMATTER;

@Slf4j
@Service
public class StatusService {


    private final LocalDateTime startedAt;
    private final String messageTemplate;
    private final String ciProvider;

    public StatusService(
            @Value("${app.status.message}") String messageTemplate,
            @Value("${app.ci-provider:локальной сборки}") String ciProvider
    ) {
        this.startedAt = LocalDateTime.now();
        this.messageTemplate = messageTemplate;
        this.ciProvider = ciProvider;
        log.info("StatusService инициализирован, время запуска приложения зафиксировано: {}", startedAt);
    }

    public String getStatusMessage() {
        return String.format(
                messageTemplate,
                ciProvider,
                startedAt.format(DATE_FORMATTER),
                startedAt.format(TIME_FORMATTER)
        );
    }
}
