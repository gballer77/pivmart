package mart.mono.commerce.cart;

import mart.mono.inventory.lib.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("api/v1/cart")
    public List<CartItem> list() {
        return cartService.get();
    }

    @PostMapping("api/v1/cart")
    public CartItem add(@RequestBody Product product) {
        return cartService.add(product);
    }

    @PutMapping("api/v1/cart/{id}")
    public List<CartItem> remove(@PathVariable(name = "id") UUID id) {
        cartService.remove(id);
        return cartService.get();
    }

    @PostMapping("api/v1/cart/checkout")
    public ResponseEntity checkOut() {
        cartService.checkOut();
        return ResponseEntity.ok().build();
    }
}
