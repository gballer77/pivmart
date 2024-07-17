package mart.mono.commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(ProductApiProperties productApiProperties, RestClient.Builder builder) {
        return builder.baseUrl(productApiProperties.getUrl()).build();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadFactory factory = Thread.ofVirtual().name("virtual-task", 1).factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }
}
