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
        return restClient.get()
            .uri("/api/products/{0}", productId)
            .retrieve()
            .body(Product.class);
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
