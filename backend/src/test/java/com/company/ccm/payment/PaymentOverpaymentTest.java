package com.company.ccm.payment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.company.ccm.support.AbstractIntegrationTest;
import com.company.ccm.support.TestAuthHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class PaymentOverpaymentTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void blocksPaymentExceedingCommittedAmount() throws Exception {
        String token = TestAuthHelper.loginAccessToken(mockMvc, objectMapper);

        mockMvc.perform(post("/api/v1/approval-definitions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "code":"CONTRACT_PAY",
                                  "name":"Contract approval",
                                  "targetType":"CONTRACT",
                                  "active":true,
                                  "steps":[{"stepOrder":1,"name":"Admin","assigneeType":"ROLE","assigneeValue":"SYSTEM_ADMIN","minApprovals":1}]
                                }
                                """))
                .andExpect(status().isOk());

        String projectId = createProject(token, "P-PAY");
        String partnerId = createPartner(token, "C-PAY");
        String contractId = createAndActivateContract(token, projectId, partnerId, "CT-PAY", "500.00");

        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "paymentNo":"PAY-1",
                                  "paymentType":"PROGRESS",
                                  "amount":600.00
                                }
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    private String createProject(String token, String code) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + code + "\",\"name\":\"" + code + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createPartner(String token, String code) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/partners")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + code + "\",\"name\":\"" + code + "\",\"type\":\"CONTRACTOR\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    private String createAndActivateContract(
            String token, String projectId, String partnerId, String contractNo, String amount) throws Exception {
        MvcResult contractResult = mockMvc.perform(post("/api/v1/projects/" + projectId + "/contracts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "partnerId":"%s",
                                  "contractNo":"%s",
                                  "title":"Pay test",
                                  "pricingType":"LUMP_SUM",
                                  "originalAmount":%s
                                }
                                """.formatted(partnerId, contractNo, amount)))
                .andExpect(status().isOk())
                .andReturn();
        String contractId =
                objectMapper.readTree(contractResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/v1/contracts/" + contractId + "/submit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MvcResult tasks = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                                        "/api/v1/approval-tasks/mine")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        String taskId = objectMapper.readTree(tasks.getResponse().getContentAsString()).get(0).get("id").asText();
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
}
