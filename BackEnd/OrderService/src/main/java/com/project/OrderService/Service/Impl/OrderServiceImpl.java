package com.project.OrderService.Service.Impl;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.Order.CreateOrderRequest;
import com.project.OrderService.DTO.Order.OrderResponse;
import com.project.OrderService.DTO.Product.ProductInfoResponse;
import com.project.OrderService.DTO.User.AddressInfoResponse;
import com.project.OrderService.Entity.*;
import com.project.OrderService.Enum.CartStatus;
import com.project.OrderService.Enum.OrderStatus;
import com.project.OrderService.Enum.PaymentStatus;
import com.project.OrderService.Exception.CartException;
import com.project.OrderService.Exception.ResourceNotFoundException;
import com.project.OrderService.Mapper.OrderMapper;
import com.project.OrderService.Repository.*;
import com.project.OrderService.Service.OrderService;
import com.project.OrderService.Service.ProductClient;
import com.project.OrderService.Service.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID buyerId, CreateOrderRequest request) {

        ApiResponse<AddressInfoResponse> addressResponse = userClient
                .getAddressForUser(buyerId, request.getAddressId());

        AddressInfoResponse address = addressResponse.getData();

        Cart cart = cartRepository.findByBuyerIdAndStatus(buyerId, CartStatus.ACTIVE)
                .orElseThrow(()-> new CartException("Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if(cartItems.isEmpty())
        {
            throw new CartException("Cart is empty");
        }

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setBuyerId(buyerId);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        for(CartItem cartItem : cartItems)
        {
            ProductInfoResponse product = productClient.getProduct(cartItem.getProductId()).getData();

            if(!"ACTIVE".equals(product.getStatus()))
            {
                throw new CartException("Product is no longer available" + product.getName());
            }

            if(product.getPrice().compareTo(cartItem.getUnitPrice()) !=0 )
            {
                throw new CartException("Price has changed fo the product");
            }

            productClient.reserveStock(product.getId(), cartItem.getQuantity());

            BigDecimal subTotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    );

            totalAmount = totalAmount.add(subTotal);

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setSellerId(product.getSellerId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subTotal);

            orderItemRepository.save(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);

        OrderAddress orderAddress = new OrderAddress();

        orderAddress.setOrder(order);
        orderAddress.setAddressLine(address.getAddressLine());
        orderAddress.setCity(address.getCity());
        orderAddress.setPostalCode(address.getPostalCode());
        orderAddress.setState(address.getState());

        orderAddressRepository.save(orderAddress);

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        return orderMapper.toResponse(
                order,
                orderAddress,
                orderItems
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID buyerId, UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(!order.getBuyerId().equals(buyerId))
        {
            throw new ResourceNotFoundException("Order not found");
        }

        OrderAddress orderAddress = orderAddressRepository.findByOrderId(orderId)
                .orElse(null);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        return orderMapper.toResponse(
                order,
                orderAddress,
                orderItems
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(UUID buyerId, Pageable pageable) {

        Page<Order> orders = orderRepository.findByBuyerId(buyerId, pageable);

        return orders.map(order -> {
            OrderAddress orderAddress = orderAddressRepository
                    .findByOrderId(order.getId())
                    .orElse(null);

            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            return orderMapper.toResponse(
                    order,
                    orderAddress,
                    orderItems
            );
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(UUID buyerId, OrderStatus status, Pageable pageable) {

        Page<Order> orders = orderRepository.findByBuyerIdAndStatus(buyerId, status, pageable);

        return orders.map(order -> {
           OrderAddress orderAddress = orderAddressRepository.findByOrderId(order.getId())
                   .orElse(null);

           List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

           return orderMapper.toResponse(
                   order,
                   orderAddress,
                   orderItems
           );
        });
    }

    @Override
    @Transactional
    public void cancelOrder(UUID buyerId, UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(!order.getBuyerId().equals(buyerId)){
            throw new ResourceNotFoundException("Order not found");
        }

        if(order.getStatus() != OrderStatus.CREATED &&
           order.getStatus() != OrderStatus.CONFIRMED &&
           order.getStatus() != OrderStatus.PROCESSING)
        {
            throw new IllegalStateException("Order cannot be cancelled in status: " + order.getStatus());
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for(OrderItem orderItem : orderItems)
        {
            productClient.releaseStock(orderItem.getProductId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}
