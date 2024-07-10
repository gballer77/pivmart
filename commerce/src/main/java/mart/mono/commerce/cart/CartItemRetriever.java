package mart.mono.commerce.cart;

import io.micrometer.observation.Observation;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import lombok.RequiredArgsConstructor;
import mart.mono.inventory.lib.IProductService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class CartItemRetriever {

    private final IProductService productService;

    @Async
    public Future<CartItem> toCartItem(CartItemEntity cartItemEntity, Observation observation) {
        ObservationThreadLocalAccessor.getInstance().setValue(observation);
        return CompletableFuture.completedFuture(CartItem.builder()
            .id(cartItemEntity.getId())
            .quantity(cartItemEntity.getQuantity())
            .product(productService.getProductById(cartItemEntity.getProduct().getId()))
            .build());
    }
}
