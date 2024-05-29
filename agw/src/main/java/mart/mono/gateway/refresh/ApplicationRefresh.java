package mart.mono.gateway.refresh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationRefresh {
    private final RefreshEndpoint contextRefresher;
    private final RestClient restClient;
    private final ConfigVersionContext configVersionContext;

    @Scheduled(fixedDelay = 60000)
    public void refreshContext() {
        PropertyResponse response = restClient.get()
            .uri("/application/default")
            .retrieve()
            .body(PropertyResponse.class);
        if (response != null) {
            handlePropertyResponse(response);
        }
    }

    private void handlePropertyResponse(PropertyResponse response) {
        String currentVersion = configVersionContext.getVersion();
        String propertyResponseVersion = response.getVersion();
        log.debug("Checking properties version {} to {}", propertyResponseVersion, currentVersion);
        if (currentVersion == null) {
            configVersionContext.storeVersion(propertyResponseVersion);
        } else if (!currentVersion.equals(propertyResponseVersion)) {
            configVersionContext.storeVersion(propertyResponseVersion);
            Collection<String> results = contextRefresher.refresh();
            log.info("Refreshed Application Properties {}", results);
        }
    }
}
