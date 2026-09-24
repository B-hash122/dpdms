package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies public service health and documentation access without bypassing
 * protected business endpoints.
 *
 * @author DPDMS
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiAndHealthTest {

    private final MockMvc mockMvc;

    /**
     * Creates the HTTP test client.
     *
     * @param mockMvc Spring MVC test client
     */
    OpenApiAndHealthTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Ensures the health endpoint is publicly available.
     *
     * @throws Exception when the request fails
     */
    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json"));
    }

    /**
     * Ensures OpenAPI JSON is publicly available.
     *
     * @throws Exception when the request fails
     */
    @Test
    void openApiDocumentIsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "DPDMS Zoonotic Disease Service API")));
    }

    /**
     * Ensures documentation access does not make business APIs public.
     *
     * @throws Exception when the request fails
     */
    @Test
    void businessApiRemainsProtected() throws Exception {
        mockMvc.perform(get("/api/zoonotic-incidents"))
                .andExpect(status().isUnauthorized());
    }
}
