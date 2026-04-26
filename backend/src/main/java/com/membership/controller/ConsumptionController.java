package com.membership.controller;

import java.util.List;

import com.membership.dto.ApiResponse;
import com.membership.dto.ConsumeRequest;
import com.membership.model.ConsumptionRecord;
import com.membership.service.ConsumptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {

    @Autowired
    private ConsumptionService consumptionService;

    @PostMapping("/stored-value")
    public ApiResponse<ConsumptionRecord> consumeStoredValue(@RequestBody ConsumeRequest request) {
        try {
            ConsumptionRecord record = consumptionService.consumeStoredValue(
                    request.getMemberId(),
                    request.getCardId(),
                    request.getStoreId(),
                    request.getServiceId(),
                    request.getServiceName(),
                    request.getAmount(),
                    request.getIdempotentKey()
            );
            return ApiResponse.success("消费成功", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/package")
    public ApiResponse<ConsumptionRecord> consumePackage(@RequestBody ConsumeRequest request) {
        try {
            ConsumptionRecord record = consumptionService.consumePackage(
                    request.getMemberId(),
                    request.getCardId(),
                    request.getStoreId(),
                    request.getServiceId(),
                    request.getServiceName(),
                    request.getTimes() != null ? request.getTimes() : 1,
                    request.getIdempotentKey()
            );
            return ApiResponse.success("核销成功", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ConsumptionRecord> getRecordById(@PathVariable String id) {
        return consumptionService.getRecordById(id)
                .map(record -> ApiResponse.success(record))
                .orElse(ApiResponse.error("消费记录不存在"));
    }

    @GetMapping("/idempotent/{key}")
    public ApiResponse<ConsumptionRecord> getByIdempotentKey(@PathVariable String key) {
        return consumptionService.getByIdempotentKey(key)
                .map(record -> ApiResponse.success(record))
                .orElse(ApiResponse.error("消费记录不存在"));
    }

    @GetMapping
    public ApiResponse<List<ConsumptionRecord>> getAllRecords(
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String cardId) {
        List<ConsumptionRecord> records;
        if (memberId != null && !memberId.isEmpty()) {
            records = consumptionService.getRecordsByMemberId(memberId);
        } else if (cardId != null && !cardId.isEmpty()) {
            records = consumptionService.getRecordsByCardId(cardId);
        } else {
            records = consumptionService.getAllRecords();
        }
        return ApiResponse.success(records);
    }
}
