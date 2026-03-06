package in.bushansirgur.foodiesapi.controller;

import in.bushansirgur.foodiesapi.model.Order;
import in.bushansirgur.foodiesapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<Order> getOrders(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId) {
        return orderService.getOrdersForUser(userId);
    }
}
