package ru.yandex.practicum.commerce.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.payment.client.OrderClient;
import ru.yandex.practicum.commerce.payment.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repo.PaymentRepository;
import ru.yandex.practicum.commerce.payment.service.exceptions.NoOrderFoundException;
import ru.yandex.practicum.commerce.payment.service.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentState;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;

import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repo;
    private final ShoppingStoreClient storeClient;
    private final OrderClient orderClient;

    private static final double TAX_RATE = 0.10; // 10% НДС

    public PaymentService(PaymentRepository repo,
                          ShoppingStoreClient storeClient,
                          OrderClient orderClient) {
        this.repo = repo;
        this.storeClient = storeClient;
        this.orderClient = orderClient;
    }

    @Transactional
    public PaymentDto createPayment(OrderDto order) {
        if (order.products() == null || order.products().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Order has no products");
        }

        // Рассчитываем стоимости
        Double productCost = calculateProductCost(order);
        Double deliveryCost = order.deliveryPrice() != null ? order.deliveryPrice() : 0.0;
        Double totalCost = calculateTotalCost(order);

        // Создаем платеж
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(order.orderId());
        payment.setTotalPayment(totalCost);
        payment.setDeliveryTotal(deliveryCost);
        payment.setFeeTotal(productCost * TAX_RATE);
        payment.setState(PaymentState.PENDING);

        repo.save(payment);
        return toDto(payment);
    }

    public Double calculateProductCost(OrderDto order) {
        if (order.products() == null || order.products().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Order has no products");
        }

        double total = 0.0;
        for (Map.Entry<UUID, Long> entry : order.products().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = storeClient.getProduct(productId);
            total += product.price().doubleValue() * quantity;
        }

        return total;
    }

    public Double calculateTotalCost(OrderDto order) {
        // Стоимость товаров
        Double productCost = calculateProductCost(order);

        // НДС (10%)
        Double tax = productCost * TAX_RATE;

        // Стоимость доставки
        Double deliveryCost = order.deliveryPrice() != null ? order.deliveryPrice() : 0.0;

        // Итого
        return productCost + tax + deliveryCost;
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = repo.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(paymentId));

        payment.setState(PaymentState.SUCCESS);
        repo.save(payment);

        // Уведомляем Order Service
        orderClient.payment(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = repo.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException(paymentId));

        payment.setState(PaymentState.FAILED);
        repo.save(payment);

        // Уведомляем Order Service
        orderClient.paymentFailed(payment.getOrderId());
    }

    private PaymentDto toDto(Payment p) {
        return new PaymentDto(
                p.getPaymentId(),
                p.getTotalPayment(),
                p.getDeliveryTotal(),
                p.getFeeTotal()
        );
    }
}