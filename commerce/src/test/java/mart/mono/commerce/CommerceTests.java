package mart.mono.commerce;

import mart.mono.commerce.cart.CartService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.TestSocketUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CommerceTests {

    static int managementPort = TestSocketUtils.findAvailableTcpPort();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CartService cartService;

    MockWebServer mockWebServer;

    @DynamicPropertySource
    static void registerDomains(DynamicPropertyRegistry registry) {
        registry.add("productapi.url", () -> "http://localhost:" + managementPort);
    }

    private static JSONObject getV1ProductObject() throws JSONException {
        JSONObject jsonPayload = new JSONObject();

        jsonPayload.put("id", "d5d6d7d8-9c8b-4d3c-9f2e-1d5c5d2c5d2a")
            .put("catalogId", "electronics")
            .put("name", "Dell Laptop")
            .put("imageSrc", "https://picsum.photos/id/2/200/300")
            .put("imageAlt", "Products Alt Text")
            .put("description", "This is a description of Dell Laptop")
            .put("price", "10.99")
            .put("quantity", 10);
        return jsonPayload;
    }

    private static JSONObject getV2ProductObject() throws JSONException {
        JSONObject jsonPayload = new JSONObject();

        jsonPayload.put("id", "d5d6d7d8-9c8b-4d3c-9f2e-1d5c5d2c5d2a")
            .put("name", "Dell Laptop")
            .put("price", "10.99");
        return jsonPayload;
    }

    @BeforeEach
    void clearCart() throws Exception {
        cartService.removeAll();

        mockWebServer = new MockWebServer();
        mockWebServer.start(managementPort);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private ResultActions addV1ItemToCart() throws Exception {
        JSONObject jsonPayload = getV1ProductObject();
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(OK.value())
            .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .setBody(jsonPayload.toString())
        );

        MockHttpServletRequestBuilder request = post("/api/v1/cart")
            .contentType(APPLICATION_JSON)
            .content(jsonPayload.toString());

        return mockMvc.perform(request);
    }

    private ResultActions addV2ItemToCart() throws Exception {
        JSONObject jsonPayload = getV2ProductObject();

        MockHttpServletRequestBuilder request = post("/api/v1/cart")
            .contentType(APPLICATION_JSON)
            .content(jsonPayload.toString());

        return mockMvc.perform(request);
    }

    @Test
    void v1AddToCartContractTest() throws Exception {
        ResultActions resultActions = addV1ItemToCart();
        resultActions.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$", hasKey("product")))
            .andExpect(jsonPath("$", hasKey("id")))
            .andExpect(jsonPath("$", hasKey("quantity")));
    }

    @Test
    void v2AddToCartContractTest() throws Exception {
        ResultActions resultActions = addV2ItemToCart();
        resultActions.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$", hasKey("product")))
            .andExpect(jsonPath("$", hasKey("id")))
            .andExpect(jsonPath("$", hasKey("quantity")));
    }

    @Test
    void removeItemFromCartContractTest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(OK.value())
            .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .setBody("{}")
        );
        ResultActions result = addV2ItemToCart();
        JSONObject resultJson = new JSONObject(result.andReturn().getResponse().getContentAsString());

        String uuid = resultJson.getString("id");

        mockMvc.perform(put("/api/v1/cart/{id}", uuid))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", equalTo(0)));
    }

    @Test
    void getCartContractTest() throws Exception {
        addV2ItemToCart();

        mockMvc.perform(get("/api/v1/cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$[0]", hasKey("product")))
            .andExpect(jsonPath("$[0]", hasKey("id")))
            .andExpect(jsonPath("$[0]", hasKey("quantity")))
            .andDo(print())
        ;
    }


    @Test
    void checkoutCartContractTest() throws Exception {
        mockMvc.perform(post("/api/v1/cart/checkout"))
            .andExpect(status().isOk());
    }
}
