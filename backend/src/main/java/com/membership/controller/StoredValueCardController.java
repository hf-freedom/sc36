package com.membership.controller;

import java.util.List;

import com.membership.dto.ApiResponse;
import com.membership.dto.CreateStoredValueCardRequest;
import com.membership.dto.RefundResult;
import com.membership.model.StoredValueCard;
import com.membership.service.StoredValueCardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stored-value-cards")
public class StoredValueCardController {

    @Autowired
    private StoredValueCardService storedValueCardService;

    @PostMapping
    public ApiResponse<StoredValueCard> createCard(@RequestBody CreateStoredValueCardRequest request) {
        try {
            StoredValueCard card = storedValueCardService.createCard(
                    request.getMemberId(),
                    request.getName(),
                    request.getRechargeAmount(),
                    request.getGiftedAmount(),
                    request.getValidFrom(),
                    request.getValidTo()
            );
            return ApiResponse.success("储值卡创建成功", card);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<StoredValueCard> getCardById(@PathVariable String id) {
        return storedValueCardService.getCardById(id)
                .map(card -> ApiResponse.success(card))
                .orElse(ApiResponse.error("储值卡不存在"));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<StoredValueCard>> getCardsByMemberId(@PathVariable String memberId) {
        List<StoredValueCard> cards = storedValueCardService.getCardsByMemberId(memberId);
        return ApiResponse.success(cards);
    }

    @GetMapping("/member/{memberId}/active")
    public ApiResponse<List<StoredValueCard>> getActiveCardsByMemberId(@PathVariable String memberId) {
        List<StoredValueCard> cards = storedValueCardService.getActiveCardsByMemberId(memberId);
        return ApiResponse.success(cards);
    }

    @PutMapping("/{id}/refund")
    public ApiResponse<RefundResult> refundCard(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean allowPrincipalRefund) {
        try {
            RefundResult result = storedValueCardService.refundCard(id, allowPrincipalRefund);
            return ApiResponse.success("退卡成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
