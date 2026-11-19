package com.company.ccm.changeorder;

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
import org.springframework.test.web.servlet.MvcResult;

class ChangeOrderIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void approvedChangeOrderUpdatesCommittedAmount() throws Exception {
        String token = TestAuthHelper.loginAccessToken(mockMvc, objectMapper);

        ensureDefinition(token, "CONTRACT_CO", "CONTRACT");
        ensureDefinition(token, "CHANGE_ORDER_DEFAULT", "CHANGE_ORDER");

        String projectId = createProject(token);
        String partnerId = createPartner(token);
        String contractId = createActivateContract(token, projectId, partnerId);

        MvcResult coResult = mockMvc.perform(post("/api/v1/contracts/" + contractId + "/change-orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "changeNo":"CO-1",
                                  "title":"Extra steel",
                                  "amountDelta":100.00,
                                  "reason":"Design change"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String changeOrderId =
                objectMapper.readTree(coResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/v1/change-orders/" + changeOrderId + "/submit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String taskId = latestTask(token);
        mockMvc.perform(post("/api/v1/approval-tasks/" + taskId + "/approve")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/contracts/" + contractId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.committedAmount").value(1100.0));
    }

    private void ensureDefinition(String token, String code, String targetType) throws Exception {
        mockMvc.perform(post("/api/v1/approval-definitions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "code":"%s",
                                  "name":"%s",
                                  "targetType":"%s",
                                  "active":true,
                                  "steps":[{"stepOrder":1,"name":"Admin","assigneeType":"ROLE","assigneeValue":"SYSTEM_ADMIN","minApprovals":1}]
                                }
                                """.formatted(code, code, targetType)))
                .andExpect(status().isOk());
    }

    private String createProject(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"P-CO\",\"name\":\"CO Project\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createPartner(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/partners")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"C-CO\",\"name\":\"CO Partner\",\"type\":\"CONTRACTOR\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createActivateContract(String token, String projectId, String partnerId) throws Exception {
        MvcResult contractResult = mockMvc.perform(post("/api/v1/projects/" + projectId + "/contracts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partnerId":"%s",
                                  "contractNo":"CT-CO",
                                  "title":"CO Contract",
                                  "pricingType":"LUMP_SUM",
                                  "originalAmount":1000.00
                                }
                                """.formatted(partnerId)))
                .andExpect(status().isOk())
                .andReturn();
        String contractId =
                objectMapper.readTree(contractResult.getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/submit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        String taskId = latestTask(token);
        mockMvc.perform(post("/api/v1/approval-tasks/" + taskId + "/approve")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/activate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        return contractId;
    }

    private String latestTask(String token) throws Exception {
        MvcResult tasks = mockMvc.perform(get("/api/v1/approval-tasks/mine").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(tasks.getResponse().getContentAsString()).get(0).get("id").asText();
    }
}
