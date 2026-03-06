package in.bushansirgur.foodiesapi.config;

import in.bushansirgur.foodiesapi.model.Product;
import in.bushansirgur.foodiesapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final ProductRepository productRepository;

    @Bean
    @ConditionalOnProperty(prefix = "foodies.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedProducts() {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }
            List<Product> products = List.of(
                    Product.builder()
                            .name("Margherita Pizza")
                            .description("Classic tomato, basil, and mozzarella")
                            .imageUrl("https://images.unsplash.com/photo-1593560708920-61dd98c46a4e")
                            .price(new BigDecimal("9.99"))
                            .build(),
                    Product.builder()
                            .name("Veggie Burger")
                            .description("Loaded with grilled veggies and house sauce")
                            .imageUrl("https://images.unsplash.com/photo-1550547660-d9450f859349")
                            .price(new BigDecimal("7.49"))
                            .build(),
                    Product.builder()
                            .name("Paneer Wrap")
                            .description("Spicy paneer, crunchy lettuce, and mint mayo")
                            .imageUrl("https://images.unsplash.com/photo-1539252554453-80ab65ce3586")
                            .price(new BigDecimal("6.75"))
                            .build()
            );
            productRepository.saveAll(products);
        };
    }
}
