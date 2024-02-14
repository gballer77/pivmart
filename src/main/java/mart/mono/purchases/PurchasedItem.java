package mart.mono.purchases;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mart.mono.cart.Product;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchased_items")
public class PurchasedItem {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;


    @JoinColumn(name ="product_id", nullable = false)
    private Product product;

    private Integer quantity;
}
