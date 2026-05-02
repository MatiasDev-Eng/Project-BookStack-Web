package com.example.bookstack_backend.services;

import com.example.bookstack_backend.dto.response.OrderDetailsResponse;
import com.example.bookstack_backend.dto.response.OrderResponse;
import com.example.bookstack_backend.models.*;
import com.example.bookstack_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final AuditLogService logService;
    private final BookRepository bookRepository;
    private final SaleNotificationService saleNotificationService;

    @Autowired
    public OrderService(CartRepository cartRepository,
                        OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        PaymentRepository paymentRepository,
                        UserRepository userRepository,
                        AuditLogService logService,
                        BookRepository bookRepository,
                        SaleNotificationService saleNotificationService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.logService = logService;
        this.bookRepository = bookRepository;
        this.saleNotificationService = saleNotificationService;
    }

    @Transactional
    public Order checkout(User user, String address, Long cardId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Can't checkout an empty cart");
        }

        CreditCard card = paymentRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        Order order = new Order();
        order.setUserOrder(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        order.setDeliveryAddress(address);
        order.setCreditCard(card);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            Book book = cartItem.getBook();
            int requestedQty = cartItem.getQuantity();

            if (book.getStock() == null || book.getStock() < requestedQty) {
                throw new RuntimeException("Not enough stock for \"" + book.getTitle() + "\". Only " + (book.getStock() == null ? 0 : book.getStock()) + " left.");
            }

            book.setStock(book.getStock() - requestedQty);

            if (book.getStock() == 0) {
                book.setIsActive(false);
            }

            bookRepository.save(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(requestedQty);
            orderItem.setPriceAtPurchase(book.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        Map<User, BigDecimal> ownerEarnings = new HashMap<>();

        orderItems.forEach(item -> {
            User bookOwner = item.getBook().getOwner();
            BigDecimal earnings = item.getPriceAtPurchase()
                    .multiply(new BigDecimal(item.getQuantity()));
            ownerEarnings.merge(bookOwner, earnings, BigDecimal::add);
        });

        ownerEarnings.forEach((owner, earnings) -> {
            owner.setBalance(owner.getBalance().add(earnings));
            userRepository.save(owner);
        });

        Order savedOrder = orderRepository.save(order);

        logService.log(user.getId(), EActionType.PURCHASE, "Order #" + savedOrder.getId() + ", total: $" + savedOrder.getTotalPrice());

        savedOrder.getOrderItems().forEach(item -> {
            User seller = item.getBook().getOwner();
            saleNotificationService.notifySeller(seller, item, address);
        });

        cartItemRepository.deleteAllByCart(cart);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<OrderResponse> getAllOrdersForUser(User user) {
        List<Order> orders = orderRepository.findByUserOrder(user);

        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    public OrderDetailsResponse getOrderDetails(Long orderId, String username) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserOrder().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return new OrderDetailsResponse(order);
    }

    @Transactional
    public Order updateStatus(Long orderId, String username, OrderStatus status) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserOrder().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        if (status == OrderStatus.DELIVERED || status == OrderStatus.IN_TRANSIT) {
            throw new RuntimeException("Only administrators can set this status.");
        }

        // ── Handle return ─────────────────────────────────────
        if (status == OrderStatus.RETURNED && order.getStatus() != OrderStatus.RETURNED) {
            Map<User, BigDecimal> ownerDeductions = new HashMap<>();

            order.getOrderItems().forEach(item -> {
                Book book = item.getBook();
                int qty    = item.getQuantity();

                if (book != null) {
                    book.setStock(book.getStock() + qty);
                    if (!Boolean.TRUE.equals(book.getIsFrozen()) && !Boolean.TRUE.equals(book.getIsDeleted())) {
                        book.setIsActive(true);
                    }
                    bookRepository.save(book);

                    User seller = book.getOwner();
                    BigDecimal deduction = item.getPriceAtPurchase().multiply(new BigDecimal(qty));
                    ownerDeductions.merge(seller, deduction, BigDecimal::add);
                }
            });

            ownerDeductions.forEach((seller, deduction) -> {
                BigDecimal newBalance = seller.getBalance().subtract(deduction);

                seller.setBalance(newBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBalance);
                userRepository.save(seller);
            });
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

}
