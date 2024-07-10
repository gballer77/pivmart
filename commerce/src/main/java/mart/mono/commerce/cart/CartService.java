package mart.mono.commerce.cart;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mart.mono.commerce.product.ProductEntity;
import mart.mono.commerce.purchase.PurchasesService;
import mart.mono.inventory.lib.IProductService;
import mart.mono.inventory.lib.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final PurchasesService purchasesService;
    private final IProductService productService;
    private final Executor taskExecutor;

    public List<CartItem> get() {
        List<Future<CartItem>> futures = cartRepository.findAll().stream()
            .map(cartItemEntity -> toCartItem(cartItemEntity))
            .toList();
        return futures.stream().map(this::getCartItem).toList();
    }

    private Future<CartItem> toCartItem(CartItemEntity cartItemEntity) {
        return CompletableFuture.supplyAsync(() -> CartItem.builder()
            .id(cartItemEntity.getId())
            .quantity(cartItemEntity.getQuantity())
            .product(productService.getProductById(cartItemEntity.getProduct().getId()))
            .build(), taskExecutor);
    }

    @SneakyThrows
    private CartItem getCartItem(Future<CartItem> cartItemFuture) {
        return cartItemFuture.get();
    }

    public CartItem add(Product product) {
        ProductEntity cartProduct = new ProductEntity(product.getId(), product.getName(), product.getPrice());
        CartItemEntity savedCartItem = cartRepository.save(CartItemEntity.builder()
            .product(cartProduct)
            .quantity(1)
            .build());
        return getCartItem(toCartItem(savedCartItem));
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
