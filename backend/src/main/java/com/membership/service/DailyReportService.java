package com.membership.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.membership.model.ConsumptionRecord;
import com.membership.model.DailyReport;
import com.membership.model.Member;
import com.membership.model.PackageCard;
import com.membership.model.RefundRecord;
import com.membership.model.StoreSettlement;
import com.membership.model.StoredValueCard;
import com.membership.repository.ConsumptionRecordRepository;
import com.membership.repository.DailyReportRepository;
import com.membership.repository.MemberRepository;
import com.membership.repository.PackageCardRepository;
import com.membership.repository.RefundRecordRepository;
import com.membership.repository.StoreSettlementRepository;
import com.membership.repository.StoredValueCardRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyReportService {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportService.class);

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    @Autowired
    private PackageCardRepository packageCardRepository;

    @Autowired
    private ConsumptionRecordRepository consumptionRecordRepository;

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Autowired
    private StoreSettlementRepository storeSettlementRepository;

    @Scheduled(cron = "0 30 0 * * ?")
    public void generateDailyReport() {
        LocalDate today = LocalDate.now();
        LocalDate reportDate = today.minusDays(1);
        logger.info("开始生成 {} 的日报表", reportDate);

        try {
            generateReportForDate(reportDate);
            logger.info("日报表生成完成: {}", reportDate);
        } catch (Exception e) {
            logger.error("生成日报表时出错: {}", e.getMessage(), e);
        }
    }

    public DailyReport generateReportForDate(LocalDate reportDate) {
        if (dailyReportRepository.existsByReportDate(reportDate)) {
            return dailyReportRepository.findByReportDate(reportDate).orElse(null);
        }

        List<Member> allMembers = memberRepository.findAll();
        List<StoredValueCard> allStoredValueCards = storedValueCardRepository.findAll();
        List<PackageCard> allPackageCards = packageCardRepository.findAll();

        List<ConsumptionRecord> dailyConsumptions = consumptionRecordRepository.findByDate(reportDate);
        List<RefundRecord> dailyRefunds = refundRecordRepository.findByDate(reportDate);
        List<StoreSettlement> dailySettlements = storeSettlementRepository.findByDate(reportDate);

        BigDecimal totalBalance = allMembers.stream()
                .map(Member::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGiftBalance = allMembers.stream()
                .map(Member::getGiftedBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConsumption = dailyConsumptions.stream()
                .map(ConsumptionRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSettlement = dailySettlements.stream()
                .map(StoreSettlement::getSettlementAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeCards = allStoredValueCards.stream()
                .filter(c -> c.getStatus() == com.membership.model.CardStatus.ACTIVE)
                .count() +
                allPackageCards.stream()
                .filter(c -> c.getStatus() == com.membership.model.CardStatus.ACTIVE)
                .count();

        DailyReport report = new DailyReport();
        report.setId(UUID.randomUUID().toString());
        report.setReportDate(reportDate);
        report.setTotalMembers(allMembers.size());
        report.setActiveCards((int) activeCards);
        report.setTotalBalance(totalBalance);
        report.setTotalGiftBalance(totalGiftBalance);
        report.setTotalConsumption(totalConsumption);
        report.setTotalSettlement(totalSettlement);
        report.setNewMembers(0);
        report.setNewCards(0);
        report.setConsumptionCount(dailyConsumptions.size());
        report.setRefundCount(dailyRefunds.size());
        report.setCreatedAt(LocalDateTime.now());

        return dailyReportRepository.save(report);
    }

    public Optional<DailyReport> getReportByDate(LocalDate reportDate) {
        return dailyReportRepository.findByReportDate(reportDate);
    }

    public List<DailyReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByDateRange(startDate, endDate);
    }
}
