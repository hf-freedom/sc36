package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.membership.dto.ConsumeResult;
import com.membership.model.ConsumptionRecord;
import com.membership.model.IdempotentRecord;
import com.membership.repository.ConsumptionRecordRepository;
import com.membership.repository.IdempotentRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionService {

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private IdempotentRecordRepository idempotentRecordRepository;

    @Autowired
    private StoredValueCardService storedValueCardService;

    @Autowired
    private PackageCardService packageCardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreSettlementService storeSettlementService;

    public ConsumptionRecord consumeStoredValue(
            String memberId,
            String cardId,
            String storeId,
            String serviceId,
            String serviceName,
            BigDecimal amount,
            String idempotentKey
    ) {
        if (idempotentKey != null && !idempotentKey.isEmpty()) {
            Optional<IdempotentRecord> existingRecord = idempotentRecordRepository.findByIdempotentKey(idempotentKey);
            if (existingRecord.isPresent()) {
                return consumptionRecordRepository.findById(existingRecord.get().getRelatedId())
                        .orElseThrow(() -> new RuntimeException("幂等记录存在但消费记录不存在"));
            }
        }

        ConsumeResult consumeResult = storedValueCardService.consumeBalance(cardId, amount);
        BigDecimal giftUsed = consumeResult.getGiftUsed();
        BigDecimal principalUsed = consumeResult.getPrincipalUsed();

        memberService.updateBalance(memberId, principalUsed.negate(), giftUsed.negate());

        ConsumptionRecord record = new ConsumptionRecord();
        record.setId(UUID.randomUUID().toString());
        record.setMemberId(memberId);
        record.setCardId(cardId);
        record.setCardType("STORED_VALUE");
        record.setStoreId(storeId);
        record.setServiceId(serviceId);
        record.setServiceName(serviceName);
        record.setAmount(amount);
        record.setPrincipalUsed(principalUsed);
        record.setGiftUsed(giftUsed);
        record.setTimesUsed(0);
        record.setIdempotentKey(idempotentKey);
        record.setConsumedAt(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setRefunded(false);

        ConsumptionRecord savedRecord = consumptionRecordRepository.save(record);

        if (idempotentKey != null && !idempotentKey.isEmpty()) {
            IdempotentRecord idempotentRecord = new IdempotentRecord();
            idempotentRecord.setId(UUID.randomUUID().toString());
            idempotentRecord.setIdempotentKey(idempotentKey);
            idempotentRecord.setOperationType("CONSUMPTION");
            idempotentRecord.setRelatedId(savedRecord.getId());
            idempotentRecord.setCreatedAt(LocalDateTime.now());
            idempotentRecordRepository.save(idempotentRecord);
        }

        storeSettlementService.createSettlement(savedRecord);

        return savedRecord;
    }

    public ConsumptionRecord consumePackage(
            String memberId,
            String cardId,
            String storeId,
            String serviceId,
            String serviceName,
            int times,
            String idempotentKey
    ) {
        if (idempotentKey != null && !idempotentKey.isEmpty()) {
            Optional<IdempotentRecord> existingRecord = idempotentRecordRepository.findByIdempotentKey(idempotentKey);
            if (existingRecord.isPresent()) {
                return consumptionRecordRepository.findById(existingRecord.get().getRelatedId())
                        .orElseThrow(() -> new RuntimeException("幂等记录存在但消费记录不存在"));
            }
        }

        int consumedTimes = packageCardService.consumeTimes(cardId, times);

        ConsumptionRecord record = new ConsumptionRecord();
        record.setId(UUID.randomUUID().toString());
        record.setMemberId(memberId);
        record.setCardId(cardId);
        record.setCardType("PACKAGE");
        record.setStoreId(storeId);
        record.setServiceId(serviceId);
        record.setServiceName(serviceName);
        record.setAmount(BigDecimal.ZERO);
        record.setPrincipalUsed(BigDecimal.ZERO);
        record.setGiftUsed(BigDecimal.ZERO);
        record.setTimesUsed(consumedTimes);
        record.setIdempotentKey(idempotentKey);
        record.setConsumedAt(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setRefunded(false);

        ConsumptionRecord savedRecord = consumptionRecordRepository.save(record);

        if (idempotentKey != null && !idempotentKey.isEmpty()) {
            IdempotentRecord idempotentRecord = new IdempotentRecord();
            idempotentRecord.setId(UUID.randomUUID().toString());
            idempotentRecord.setIdempotentKey(idempotentKey);
            idempotentRecord.setOperationType("CONSUMPTION");
            idempotentRecord.setRelatedId(savedRecord.getId());
            idempotentRecord.setCreatedAt(LocalDateTime.now());
            idempotentRecordRepository.save(idempotentRecord);
        }

        storeSettlementService.createSettlement(savedRecord);

        return savedRecord;
    }

    public Optional<ConsumptionRecord> getRecordById(String id) {
        return consumptionRecordRepository.findById(id);
    }

    public Optional<ConsumptionRecord> getByIdempotentKey(String idempotentKey) {
        return consumptionRecordRepository.findByIdempotentKey(idempotentKey);
    }

    public List<ConsumptionRecord> getAllRecords() {
        return consumptionRecordRepository.findAll();
    }

    public List<ConsumptionRecord> getRecordsByMemberId(String memberId) {
        return consumptionRecordRepository.findByMemberId(memberId);
    }

    public List<ConsumptionRecord> getRecordsByCardId(String cardId) {
        return consumptionRecordRepository.findByCardId(cardId);
    }
}
