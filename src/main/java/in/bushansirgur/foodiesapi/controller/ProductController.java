package in.bushansirgur.foodiesapi.controller;

import in.bushansirgur.foodiesapi.dto.CreateProductRequest;
import in.bushansirgur.foodiesapi.model.Product;
import in.bushansirgur.foodiesapi.service.ProductCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductCatalogService productCatalogService;

    @GetMapping
    public List<Product> getProducts() {
        return productCatalogService.getAllProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productCatalogService.createProduct(request);
    }
}
