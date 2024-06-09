package com.example.order_service.service;

import com.example.order_service.dto.InventoryResponse;
import com.example.order_service.dto.OrderLineItemsDto;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderLineItems;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) throws OutOfStockException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderRequest
                .getOrderLineItemsDtoList()
                .stream().map(this::mapToDto).toList());

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();


        // call the inventory service, and place order if product is in stock.
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new OutOfStockException("Product not in stock");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .id(orderLineItemsDto.getId())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }
}
