package in.bushansirgur.foodiesapi.controller;

import in.bushansirgur.foodiesapi.dto.AddCartItemRequest;
import in.bushansirgur.foodiesapi.dto.CheckoutRequest;
import in.bushansirgur.foodiesapi.dto.UpdateCartItemRequest;
import in.bushansirgur.foodiesapi.model.Cart;
import in.bushansirgur.foodiesapi.model.Order;
import in.bushansirgur.foodiesapi.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public Cart getCart(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    public Cart addItem(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
                        @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, request.getProductId(), request.getQuantity());
    }

    @PutMapping("/items/{productId}")
    public Cart updateItem(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
                           @PathVariable String productId,
                           @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItemQuantity(userId, productId, request.getQuantity());
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
                           @PathVariable String productId) {
        cartService.removeItem(userId, productId);
    }

    @PostMapping("/checkout")
    public Order checkout(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
                          @Valid @RequestBody CheckoutRequest request) {
        return cartService.checkout(userId, request);
    }
}
