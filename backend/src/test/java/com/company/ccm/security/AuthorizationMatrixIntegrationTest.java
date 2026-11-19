package com.company.ccm.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.ccm.support.AbstractIntegrationTest;
import com.company.ccm.support.TestAuthHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class AuthorizationMatrixIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void viewerCannotCreateUsers() throws Exception {
        String adminToken = TestAuthHelper.loginAccessToken(mockMvc, objectMapper);

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "username":"viewer1",
                                  "password":"Viewer@123",
                                  "displayName":"Viewer One",
                                  "roleCodes":["VIEWER"]
                                }
                                """))
                .andExpect(status().isOk());

        String viewerToken = TestAuthHelper.loginAccessToken(mockMvc, objectMapper, "viewer1", "Viewer@123");

        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + viewerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", "Bearer " + viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "username":"x",
                                  "password":"X@123456",
                                  "displayName":"X",
                                  "roleCodes":["VIEWER"]
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
