package in.bushansirgur.foodiesapi.repository;

import in.bushansirgur.foodiesapi.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
