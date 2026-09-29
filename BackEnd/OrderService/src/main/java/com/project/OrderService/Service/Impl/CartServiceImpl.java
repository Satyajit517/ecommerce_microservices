package com.project.OrderService.Service.Impl;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.Cart.CartResponse;
import com.project.OrderService.DTO.Product.ProductInfoResponse;
import com.project.OrderService.Entity.Cart;
import com.project.OrderService.Entity.CartItem;
import com.project.OrderService.Enum.CartStatus;
import com.project.OrderService.Exception.CartException;
import com.project.OrderService.Exception.ResourceNotFoundException;
import com.project.OrderService.Mapper.CartMapper;
import com.project.OrderService.Repository.CartItemRepository;
import com.project.OrderService.Repository.CartRepository;
import com.project.OrderService.Service.CartService;
import com.project.OrderService.Service.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UUID buyerId) {

        Cart cart = cartRepository
                .findByBuyerIdAndStatus(buyerId, CartStatus.ACTIVE)
                .orElseGet(()->
                {
                   Cart newCart = new Cart();

                   newCart.setBuyerId(buyerId);
                   newCart.setStatus(CartStatus.ACTIVE);
                   newCart.setCreatedAt(Instant.now());
                   newCart.setUpdatedAt(Instant.now());

                   return cartRepository.save(newCart);
                });

        Map<UUID, ProductInfoResponse> productMap = getProductMap(cart);

        return cartMapper.toResponse(cart, productMap);
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID buyerId, UUID productId, int quantity) {

        validateQuantity(quantity);

        ApiResponse<ProductInfoResponse> productResponse = productClient.getProduct(productId);

        ProductInfoResponse product = productResponse.getData();

        if(!"ACTIVE".equals(product.getStatus()))
        {
            throw new CartException("Product is not available for purchase");
        }

        Cart cart = getOrCreateActiveCart(buyerId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if(cartItem != null)
        {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setUpdatedAt(Instant.now());
        }
        else
        {
            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setCreatedAt(Instant.now());
            cartItem.setUpdatedAt(Instant.now());
        }

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        Map<UUID, ProductInfoResponse> productMap = getProductMap(cart);

        return cartMapper.toResponse(cart, productMap);
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID buyerId, UUID productId, int quantity) {

        validateQuantity(quantity);

        Cart cart = getActiveCart(buyerId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(()-> new CartException("Product is not present in the cart"));

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(Instant.now());

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        Map<UUID, ProductInfoResponse> productMap = getProductMap(cart);

        return cartMapper.toResponse(cart, productMap);
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID buyerId, UUID productId) {

        Cart cart = getActiveCart(buyerId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(()-> new CartException("Product is not present in the Cart"));

        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);

        Map<UUID, ProductInfoResponse> productMap = getProductMap(cart);

        return cartMapper.toResponse(cart, productMap);
    }

    @Override
    @Transactional
    public void clearCart(UUID buyerId) {

        Cart cart = getActiveCart(buyerId);

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart);
    }


    void validateQuantity(int quantity)
    {
        if(quantity <= 0)
        {
            throw new IllegalArgumentException("Quantity must be greater than Zero");
        }
    }

    private Cart getOrCreateActiveCart(UUID id)
    {
        return cartRepository
                .findByBuyerIdAndStatus(id, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setBuyerId(id);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setCreatedAt(Instant.now());
                    cart.setUpdatedAt(Instant.now());

                    return cartRepository.save(cart);
                });
    }

    private Cart getActiveCart(UUID buyerId) {

        return cartRepository
                .findByBuyerIdAndStatus(buyerId, CartStatus.ACTIVE)
                .orElseThrow(()->
                        new ResourceNotFoundException("Active cart not found"));
    }


    private Map<UUID, ProductInfoResponse> getProductMap(Cart cart) {
        List<UUID> productIds = cart.getItems()
                .stream()
                .map(CartItem::getProductId)
                .toList();

        ApiResponse<List<ProductInfoResponse>> response = productClient.getProducts(productIds);

        return response.getData()
                .stream()
                .collect(Collectors.toMap(
                        ProductInfoResponse::getId,
                        product -> product
                ));
    }

}
