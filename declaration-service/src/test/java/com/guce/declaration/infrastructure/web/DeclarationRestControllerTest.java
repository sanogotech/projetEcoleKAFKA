package com.guce.declaration.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guce.declaration.application.port.in.SubmitDeclarationUseCase;
import com.guce.declaration.application.service.GetDeclarationService;
import com.guce.declaration.domain.Declaration;
import com.guce.declaration.domain.DeclarationStatus;
import com.guce.declaration.infrastructure.web.dto.SubmitDeclarationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeclarationRestController.class)
class DeclarationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubmitDeclarationUseCase submitDeclarationUseCase;

    @MockBean
    private GetDeclarationService getDeclarationService;

    @Test
    void submitReturnsCreated() throws Exception {
        UUID id = UUID.randomUUID();
        Declaration saved = new Declaration(
                id,
                "corr-1",
                "decl-1",
                "CIAB1",
                "REF-1",
                "{\"x\":1}",
                DeclarationStatus.SOUMISE,
                Instant.parse("2026-01-01T00:00:00Z"));
        when(submitDeclarationUseCase.submit(any())).thenReturn(saved);

        SubmitDeclarationRequest req = new SubmitDeclarationRequest(
                "corr-1", "decl-1", "CIAB1", "REF-1", "{\"x\":1}");

        mockMvc.perform(post("/api/v1/declarations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.correlationId").value("corr-1"));
    }

    @Test
    void getByIdReturnsNotFound() throws Exception {
        when(getDeclarationService.getById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/declarations/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
