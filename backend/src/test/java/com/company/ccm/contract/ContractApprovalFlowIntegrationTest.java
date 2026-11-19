package com.company.ccm.contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.ccm.support.AbstractIntegrationTest;
import com.company.ccm.support.TestAuthHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class ContractApprovalFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createSubmitApproveActivateContract() throws Exception {
        String token = TestAuthHelper.loginAccessToken(mockMvc, objectMapper);

        mockMvc.perform(post("/api/v1/approval-definitions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "code":"CONTRACT_DEFAULT",
                                  "name":"Contract approval",
                                  "targetType":"CONTRACT",
                                  "active":true,
                                  "steps":[{"stepOrder":1,"name":"Admin approve","assigneeType":"ROLE","assigneeValue":"SYSTEM_ADMIN","minApprovals":1}]
                                }
                                """))
                .andExpect(status().isOk());

        MvcResult projectResult = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"P-200\",\"name\":\"Bridge\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String projectId = objectMapper.readTree(projectResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult partnerResult = mockMvc.perform(post("/api/v1/partners")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"C-2\",\"name\":\"Partner B\",\"type\":\"CONTRACTOR\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String partnerId = objectMapper.readTree(partnerResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult contractResult = mockMvc.perform(post("/api/v1/projects/" + projectId + "/contracts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partnerId":"%s",
                                  "contractNo":"CT-001",
                                  "title":"Main works",
                                  "pricingType":"LUMP_SUM",
                                  "originalAmount":1000000.00
                                }
                                """.formatted(partnerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();
        String contractId =
                objectMapper.readTree(contractResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/submit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));

        MvcResult tasks = mockMvc.perform(get("/api/v1/approval-tasks/mine")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode taskArray = objectMapper.readTree(tasks.getResponse().getContentAsString());
        String taskId = taskArray.get(0).get("id").asText();

        mockMvc.perform(post("/api/v1/approval-tasks/" + taskId + "/approve")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"ok\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/contracts/" + contractId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/activate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}
