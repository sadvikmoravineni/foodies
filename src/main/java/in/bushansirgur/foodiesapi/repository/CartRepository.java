package in.bushansirgur.foodiesapi.repository;

import in.bushansirgur.foodiesapi.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart, String> {
    List<Cart> findAllByUserId(String userId);
}
