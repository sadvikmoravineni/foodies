package in.bushansirgur.foodiesapi.service;

import in.bushansirgur.foodiesapi.model.Cart;
import in.bushansirgur.foodiesapi.model.CartItem;
import in.bushansirgur.foodiesapi.model.Order;
import in.bushansirgur.foodiesapi.model.OrderItem;
import in.bushansirgur.foodiesapi.model.Product;
import in.bushansirgur.foodiesapi.dto.CheckoutRequest;
import in.bushansirgur.foodiesapi.repository.CartRepository;
import in.bushansirgur.foodiesapi.repository.OrderRepository;
import in.bushansirgur.foodiesapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Cart getCart(String userId) {
        List<Cart> carts = cartRepository.findAllByUserId(userId);
        if (carts.isEmpty()) {
            return cartRepository.save(Cart.builder().userId(userId).build());
        }
        if (carts.size() == 1) {
            Cart cart = carts.get(0);
            recalculateCartTotal(cart);
            return cartRepository.save(cart);
        }

        Cart primary = carts.get(0);
        Map<String, CartItem> mergedItems = new LinkedHashMap<>();
        for (Cart cart : carts) {
            if (cart.getItems() == null) {
                continue;
            }
            for (CartItem item : cart.getItems()) {
                CartItem existing = mergedItems.get(item.getProductId());
                if (existing == null) {
                    mergedItems.put(item.getProductId(), CartItem.builder()
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .lineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build());
                } else {
                    int mergedQty = existing.getQuantity() + item.getQuantity();
                    existing.setQuantity(mergedQty);
                    existing.setLineTotal(existing.getUnitPrice().multiply(BigDecimal.valueOf(mergedQty)));
                }
            }
        }

        primary.setItems(new ArrayList<>(mergedItems.values()));
        recalculateCartTotal(primary);
        Cart savedPrimary = cartRepository.save(primary);

        List<Cart> duplicates = carts.subList(1, carts.size());
        cartRepository.deleteAll(duplicates);
        return savedPrimary;
    }

    public Cart addItem(String userId, String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Cart cart = getCart(userId);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            item.setQuantity(newQuantity);
            item.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .lineTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();
            cart.getItems().add(newItem);
        }

        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(String userId, String productId, int quantity) {
        Cart cart = getCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not in cart"));

        item.setQuantity(quantity);
        item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public Cart removeItem(String userId, String productId) {
        Cart cart = getCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not in cart");
        }
        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public Order checkout(String userId, CheckoutRequest request) {
        Cart cart = getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
        if (!"COD".equalsIgnoreCase(request.getPaymentMethod())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only COD payment is supported");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .lineTotal(cartItem.getLineTotal())
                    .build());
        }

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .total(cart.getTotal())
                .createdAt(Instant.now())
                .status("PLACED")
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .paymentMethod("COD")
                .build();

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        return savedOrder;
    }

    private void recalculateCartTotal(Cart cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotal(total);
    }
}
