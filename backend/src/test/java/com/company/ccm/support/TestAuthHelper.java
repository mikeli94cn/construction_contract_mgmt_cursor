package com.company.ccm.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class TestAuthHelper {

    private TestAuthHelper() {}

    public static String loginAccessToken(MockMvc mockMvc, ObjectMapper objectMapper) throws Exception {
        return loginAccessToken(mockMvc, objectMapper, "admin", "Admin@123");
    }

    public static String loginAccessToken(MockMvc mockMvc, ObjectMapper objectMapper, String username, String password)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("accessToken").asText();
    }
}
