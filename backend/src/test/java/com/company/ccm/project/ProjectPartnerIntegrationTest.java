package com.company.ccm.project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.ccm.support.AbstractIntegrationTest;
import com.company.ccm.support.TestAuthHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ProjectPartnerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createProjectAndPartner() throws Exception {
        String token = TestAuthHelper.loginAccessToken(mockMvc, objectMapper);

        mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"P-100\",\"name\":\"Tower A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P-100"))
                .andExpect(jsonPath("$.members").isArray());

        mockMvc.perform(post("/api/v1/partners")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"code\":\"C-1\",\"name\":\"Builder Co\",\"type\":\"CONTRACTOR\",\"contactName\":\"Li\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CONTRACTOR"));

        mockMvc.perform(get("/api/v1/projects").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code=='P-100')]").isNotEmpty());
    }
}
