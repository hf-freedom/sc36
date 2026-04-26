package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.membership.model.PaymentOrder;
import com.membership.model.PaymentStatus;
import com.membership.repository.PaymentOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    private StoredValueCardService storedValueCardService;

    @Autowired
    private MemberService memberService;

    public PaymentOrder createOrder(
            String memberId,
            String orderType,
            String relatedCardId,
            BigDecimal amount,
            BigDecimal giftedAmount,
            String paymentMethod
    ) {
        PaymentOrder order = new PaymentOrder();
        order.setId(UUID.randomUUID().toString());
        order.setMemberId(memberId);
        order.setOrderType(orderType);
        order.setRelatedCardId(relatedCardId);
        order.setAmount(amount);
        order.setGiftedAmount(giftedAmount != null ? giftedAmount : BigDecimal.ZERO);
        order.setStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(paymentMethod);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        return paymentOrderRepository.save(order);
    }

    public PaymentOrder payOrder(String orderId) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("支付订单不存在"));

        if (order.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("订单状态异常，无法支付");
        }

        order.setStatus(PaymentStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        PaymentOrder paidOrder = paymentOrderRepository.save(order);

        if ("STORED_VALUE".equals(order.getOrderType())) {
            storedValueCardService.addBalance(
                    order.getRelatedCardId(),
                    order.getAmount(),
                    order.getGiftedAmount()
            );
            memberService.updateBalance(
                    order.getMemberId(),
                    order.getAmount(),
                    order.getGiftedAmount()
            );
        }

        return paidOrder;
    }

    public PaymentOrder cancelOrder(String orderId) {
        PaymentOrder order = paymentOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("支付订单不存在"));

        if (order.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("订单状态异常，无法取消");
        }

        order.setStatus(PaymentStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return paymentOrderRepository.save(order);
    }

    public Optional<PaymentOrder> getOrderById(String id) {
        return paymentOrderRepository.findById(id);
    }
}
