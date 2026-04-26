package com.membership.controller;

import com.membership.dto.ApiResponse;
import com.membership.dto.RefundRequest;
import com.membership.model.RefundRecord;
import com.membership.service.RefundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @PostMapping
    public ApiResponse<RefundRecord> refund(@RequestBody RefundRequest request) {
        try {
            RefundRecord refund = refundService.refundConsumption(
                    request.getConsumptionId(),
                    request.getReason()
            );
            return ApiResponse.success("退款成功", refund);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<RefundRecord> getRefundById(@PathVariable String id) {
        return refundService.getRefundById(id)
                .map(refund -> ApiResponse.success(refund))
                .orElse(ApiResponse.error("退款记录不存在"));
    }

    @GetMapping("/consumption/{consumptionId}")
    public ApiResponse<RefundRecord> getRefundByConsumptionId(@PathVariable String consumptionId) {
        return refundService.getRefundByConsumptionId(consumptionId)
                .map(refund -> ApiResponse.success(refund))
                .orElse(ApiResponse.error("退款记录不存在"));
    }
}
