package mart.mono.commerce.cart;

import io.micrometer.observation.Observation;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mart.mono.commerce.product.ProductEntity;
import mart.mono.commerce.purchase.PurchasesService;
import mart.mono.inventory.lib.Product;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Profile("async")
@Slf4j
public class CartAsyncService implements CartService {

    private final CartRepository cartRepository;
    private final PurchasesService purchasesService;
    private final CartItemRetriever cartItemRetriever;

    public List<CartItem> get() {
        Observation value = ObservationThreadLocalAccessor.getInstance().getValue();
        List<Future<CartItem>> futuresList = cartRepository.findAll().stream()
            .map(cartItem -> cartItemRetriever.toCartItem(cartItem, value))
            .toList();

        return futuresList.stream()
            .map(this::getCartItemFromFuture)
            .toList();
    }

    @SneakyThrows
    private CartItem getCartItemFromFuture(Future<CartItem> cartItemCompletableFuture) {
        return cartItemCompletableFuture.get();
    }

    @SneakyThrows
    public CartItem add(Product product) {
        Observation value = ObservationThreadLocalAccessor.getInstance().getValue();
        ProductEntity cartProduct = new ProductEntity(product.getId(), product.getName(), product.getPrice());
        CartItemEntity savedCartItem = cartRepository.save(CartItemEntity.builder()
            .product(cartProduct)
            .quantity(1)
            .build());
        return cartItemRetriever.toCartItem(savedCartItem, value).get();
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
