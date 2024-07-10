package mart.mono.commerce.config;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextScheduledExecutorService;
import io.micrometer.context.ContextSnapshot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(ProductApiProperties productApiProperties, RestClient.Builder builder) {
        return builder.baseUrl(productApiProperties.getUrl())
            .build();
    }

    @Bean(name = "taskExecutor", destroyMethod = "shutdown")
    ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler() {
            @Override
            protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                ExecutorService executorService = super.initializeExecutor(threadFactory, rejectedExecutionHandler);
                return ContextExecutorService.wrap(executorService, ContextSnapshot::captureAll);
            }
            @Override
            public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
                return ContextScheduledExecutorService.wrap(super.getScheduledExecutor());
            }
        };
        threadPoolTaskScheduler.setPoolSize(20);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
