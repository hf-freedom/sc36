package com.membership.controller;

import java.util.List;

import com.membership.dto.ApiResponse;
import com.membership.dto.CreatePackageCardRequest;
import com.membership.dto.RefundResult;
import com.membership.model.PackageCard;
import com.membership.service.PackageCardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/package-cards")
public class PackageCardController {

    @Autowired
    private PackageCardService packageCardService;

    @PostMapping
    public ApiResponse<PackageCard> createCard(@RequestBody CreatePackageCardRequest request) {
        try {
            PackageCard card = packageCardService.createCard(
                    request.getMemberId(),
                    request.getName(),
                    request.getPackageId(),
                    request.getPrice(),
                    request.getTotalTimes(),
                    request.getServiceIds(),
                    request.getValidFrom(),
                    request.getValidTo()
            );
            return ApiResponse.success("套餐卡创建成功", card);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<PackageCard> getCardById(@PathVariable String id) {
        return packageCardService.getCardById(id)
                .map(card -> ApiResponse.success(card))
                .orElse(ApiResponse.error("套餐卡不存在"));
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<List<PackageCard>> getCardsByMemberId(@PathVariable String memberId) {
        List<PackageCard> cards = packageCardService.getCardsByMemberId(memberId);
        return ApiResponse.success(cards);
    }

    @GetMapping("/member/{memberId}/active")
    public ApiResponse<List<PackageCard>> getActiveCardsByMemberId(@PathVariable String memberId) {
        List<PackageCard> cards = packageCardService.getActiveCardsByMemberId(memberId);
        return ApiResponse.success(cards);
    }

    @GetMapping("/member/{memberId}/service/{serviceId}")
    public ApiResponse<List<PackageCard>> getCardsByMemberAndService(
            @PathVariable String memberId,
            @PathVariable String serviceId) {
        List<PackageCard> cards = packageCardService.getCardsByMemberAndService(memberId, serviceId);
        return ApiResponse.success(cards);
    }

    @PutMapping("/{id}/refund")
    public ApiResponse<RefundResult> refundCard(@PathVariable String id) {
        try {
            RefundResult result = packageCardService.refundCard(id);
            return ApiResponse.success("退卡成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
