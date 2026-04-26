package com.membership.controller;

import com.membership.dto.ApiResponse;
import com.membership.dto.PayRequest;
import com.membership.model.PaymentOrder;
import com.membership.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentOrder> createOrder(@RequestBody PayRequest request) {
        try {
            PaymentOrder order = paymentService.createOrder(
                    request.getMemberId(),
                    request.getOrderType(),
                    request.getRelatedCardId(),
                    request.getAmount(),
                    request.getGiftedAmount(),
                    request.getPaymentMethod()
            );
            return ApiResponse.success("支付订单创建成功", order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentOrder> getOrderById(@PathVariable String id) {
        return paymentService.getOrderById(id)
                .map(order -> ApiResponse.success(order))
                .orElse(ApiResponse.error("支付订单不存在"));
    }

    @PutMapping("/{id}/pay")
    public ApiResponse<PaymentOrder> payOrder(@PathVariable String id) {
        try {
            PaymentOrder order = paymentService.payOrder(id);
            return ApiResponse.success("支付成功", order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<PaymentOrder> cancelOrder(@PathVariable String id) {
        try {
            PaymentOrder order = paymentService.cancelOrder(id);
            return ApiResponse.success("订单已取消", order);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
