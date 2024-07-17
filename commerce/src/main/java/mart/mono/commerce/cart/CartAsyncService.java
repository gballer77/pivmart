package mart.mono.commerce.cart;

import io.micrometer.observation.Observation;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mart.mono.commerce.product.ProductEntity;
import mart.mono.commerce.purchase.PurchasesService;
import mart.mono.inventory.lib.IProductService;
import mart.mono.inventory.lib.Product;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Profile("async")
@Slf4j
public class CartAsyncService implements CartService {

    private final CartRepository cartRepository;
    private final PurchasesService purchasesService;
    private final IProductService productService;
    private final Executor taskExecutor;

    public List<CartItem> get() {
        Observation observation = ObservationThreadLocalAccessor.getInstance().getValue();
        List<Future<CartItem>> futuresList = cartRepository.findAll().stream()
            .map(cartItem -> toCartItem(cartItem, observation))
            .toList();

        return futuresList.stream()
            .map(this::getCartItemFromFuture)
            .toList();
    }

    private Future<CartItem> toCartItem(CartItemEntity cartItemEntity, Observation observation) {
        return CompletableFuture.supplyAsync(() -> {
            ObservationThreadLocalAccessor.getInstance().setValue(observation);
            return CartItem.builder()
                .id(cartItemEntity.getId())
                .quantity(cartItemEntity.getQuantity())
                .product(productService.getProductById(cartItemEntity.getProduct().getId()))
                .build();
        }, taskExecutor);
    }

    @SneakyThrows
    public CartItem add(Product product) {
        Observation observation = ObservationThreadLocalAccessor.getInstance().getValue();
        ProductEntity cartProduct = new ProductEntity(product.getId(), product.getName(), product.getPrice());
        CartItemEntity savedCartItem = cartRepository.save(CartItemEntity.builder()
            .product(cartProduct)
            .quantity(1)
            .build());
        return getCartItemFromFuture(toCartItem(savedCartItem, observation));
    }

    @SneakyThrows
    private CartItem getCartItemFromFuture(Future<CartItem> cartItemCompletableFuture) {
        return cartItemCompletableFuture.get();
    }

    public void remove(UUID cartItemId) {
        Optional<CartItemEntity> cartItem = cartRepository.findById(cartItemId);
        cartItem.ifPresent(cartRepository::delete);
    }

    public void removeAll() {
        cartRepository.deleteAll();
    }

    public void checkOut() {
        List<CartItemEntity> cart = cartRepository.findAll();
        boolean purchaseSuccess = purchasesService.purchase(cart);
        if (purchaseSuccess) {
            cartRepository.deleteAll();
        }
    }
}
