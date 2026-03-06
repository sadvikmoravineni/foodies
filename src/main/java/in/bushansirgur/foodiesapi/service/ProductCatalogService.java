package in.bushansirgur.foodiesapi.service;

import in.bushansirgur.foodiesapi.dto.CreateProductRequest;
import in.bushansirgur.foodiesapi.model.Product;
import in.bushansirgur.foodiesapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCatalogService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .build();
        return productRepository.save(product);
    }
}
