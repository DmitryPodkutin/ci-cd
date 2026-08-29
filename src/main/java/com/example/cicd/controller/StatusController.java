package com.example.cicd.controller;

import com.example.cicd.service.StatusService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.cicd.component.DateTimeFormat.DATE_FORMATTER;
import static com.example.cicd.component.DateTimeFormat.DATE_TIME_FORMATTER;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatusController {


    private final StatusService statusService;

    @GetMapping("/status")
    public String getStatus(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("Получен запрос GET /status от IP {} в {}", ip, LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return statusService.getStatusMessage();
    }
}
