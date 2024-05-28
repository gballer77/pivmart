package mart.mono.commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(ProductApiProperties productApiProperties, RestClient.Builder builder) {
        return builder.baseUrl(productApiProperties.getUrl()).build();
    }
}
