package mart.mono.inventory.product;

import lombok.extern.slf4j.Slf4j;
import mart.mono.inventory.lib.IProductService;
import mart.mono.inventory.lib.Product;
import mart.mono.inventory.lib.PurchaseEvent;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService implements IProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
            .map(this::toProduct)
            .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404)));
    }

    public List<Product> getForCatalog(String catalogKey) {
        return productRepository.findByCatalogId(catalogKey).stream()
            .map(this::toProduct)
            .toList();
    }

    public List<Product> getAll() {
        return productRepository.findAll().stream()
            .map(this::toProduct)
            .toList();
    }

    private Product toProduct(ProductEntity productEntity) {
        return Product.builder()
            .id(productEntity.getId())
            .name(productEntity.getName())
            .description(productEntity.getDescription())
            .imageSrc(productEntity.getImageSrc())
            .imageAlt(productEntity.getImageAlt())
            .quantity(productEntity.getQuantity())
            .price(productEntity.getPrice())
            .build();
    }

    public void decrementProductQuantity(UUID productId, int quantity) {
        productRepository.findById(productId).ifPresent(productEntity -> {
            int currentQuantity = productEntity.getQuantity();
            int newQuantity = currentQuantity - quantity;
            if (newQuantity < 0) {
                return;
            }
            productEntity.setQuantity(newQuantity);
            productRepository.save(productEntity);
        });
    }

    public void processPurchaseEvent(PurchaseEvent purchaseEvent) {
        log.info("Processing Event {}", purchaseEvent);
        decrementProductQuantity(purchaseEvent.getProductId(), purchaseEvent.getQuantity());
    }
}
