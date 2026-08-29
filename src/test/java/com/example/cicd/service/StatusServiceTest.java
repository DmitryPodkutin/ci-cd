package com.example.cicd.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class StatusServiceTest {

    private static final String MESSAGE_TEMPLATE =
            "Приложение собрано с помощью %s, запущено %s в %s и находится в рабочем состоянии";

    private static final Pattern STATUS_PATTERN = Pattern.compile(
            "Приложение собрано с помощью (?<ci>.+?), " +
                    "запущено (?<date>\\d{2}-\\d{4}-\\d{2}) " +
                    "в (?<time>\\d{2}:\\d{2}:\\d{2}) и находится в рабочем состоянии"
    );

    @Test
    @DisplayName("getStatusMessage подставляет CI-провайдера, дату и время в правильном формате")
    void shouldSubstituteCiProviderDateAndTime() {
        StatusService service = new StatusService(MESSAGE_TEMPLATE, "GitHub CI");

        String result = service.getStatusMessage();

        var matcher = STATUS_PATTERN.matcher(result);
        assertThat(matcher.matches())
                .as("сообщение должно соответствовать шаблону: %s", result)
                .isTrue();
        assertThat(matcher.group("ci")).isEqualTo("GitHub CI");
    }

    @Test
    @DisplayName("getStatusMessage подставляет значение CI-провайдера по умолчанию")
    void shouldUseDefaultCiProvider() {
        StatusService service = new StatusService(MESSAGE_TEMPLATE, "локальной сборки");

        String result = service.getStatusMessage();

        assertThat(result).contains("с помощью локальной сборки");
        assertThat(result).contains("в рабочем состоянии");
        assertThat(STATUS_PATTERN.matcher(result).matches())
                .as("сообщение должно соответствовать шаблону: %s", result)
                .isTrue();
    }
}
