package com.example.cicd.controller;

import com.example.cicd.service.StatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatusService statusService;

    @Test
    @DisplayName("GET /status возвращает 200 и сообщение из сервиса")
    void shouldReturnStatusMessage() throws Exception {
        String message = "Приложение собрано с помощью Jenkins CI, запущено 29-2026-08 в 14:28:42 и находится в рабочем состоянии";
        when(statusService.getStatusMessage()).thenReturn(message);

        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(message));
    }

    @Test
    @DisplayName("GET /status делегирует вызов в StatusService")
    void shouldDelegateToStatusService() throws Exception {
        when(statusService.getStatusMessage()).thenReturn("test");

        mockMvc.perform(get("/status"));

        org.mockito.Mockito.verify(statusService).getStatusMessage();
    }
}
