package mart.mono.commerce.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mart.mono.inventory.lib.IProductService;
import mart.mono.inventory.lib.Product;
import mart.mono.inventory.lib.PurchaseEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService {
    public static final String PURCHASE_EVENT = "purchaseEvent";

    private final StreamBridge streamBridge;
    private final RestClient restClient;

    @Override
    public Product getProductById(UUID productId) {
        log.info("Retrieving product details for {}", productId);
        return getProductById(productId, 3);
    }

    private Product getProductById(UUID productId, int retryCount) {
        try {
            return restClient.get()
                .uri("/api/products/{0}", productId)
                .retrieve()
                .body(Product.class);
        } catch (Exception e) {
            log.error("Error retrieving product details for {}", productId, e);
            if (retryCount > 0) {
                return getProductById(productId, retryCount - 1);
            }
            throw e;
        }
    }

    @Override
    public void decrementProductQuantity(UUID productId, int quantity) {
        PurchaseEvent purchaseEvent = PurchaseEvent.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
        log.info("Publishing Event {}", purchaseEvent);
        streamBridge.send(PURCHASE_EVENT, purchaseEvent);
    }
}
