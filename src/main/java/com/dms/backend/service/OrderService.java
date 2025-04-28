package com.dms.backend.service;

import com.dms.backend.model.Order;
import com.dms.backend.model.ChatRoom;
import com.dms.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ChatService chatService;

    @Transactional
    public void assignDriver(Long orderId, String driverId, String managerId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDriverId(driverId);
        order.setStatus("Assigned");
        orderRepository.save(order);

        // Find or create chat room between manager and customer
        ChatRoom room = chatService.createOrGetChatRoom(
            "manager-customer", managerId, order.getCustomerId()
        );
        // Send automated message to customer
        chatService.sendMessage(
            String.valueOf(room.getId()),
            managerId,
            "Your order " + order.getId() + " has been assigned to driver " + driverId + ".",
            null,
            null
        );
    }

    public Order createOrder(Order order) {
        order.setStatus("Unassigned");
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
} 