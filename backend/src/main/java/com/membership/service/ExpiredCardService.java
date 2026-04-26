package com.membership.service;

import java.util.List;

import com.membership.model.PackageCard;
import com.membership.model.StoredValueCard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredCardService {

    private static final Logger logger = LoggerFactory.getLogger(ExpiredCardService.class);

    @Autowired
    private StoredValueCardService storedValueCardService;

    @Autowired
    private PackageCardService packageCardService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void processExpiredCards() {
        logger.info("开始处理过期卡");
        
        processExpiredStoredValueCards();
        processExpiredPackageCards();
        
        logger.info("过期卡处理完成");
    }

    private void processExpiredStoredValueCards() {
        List<StoredValueCard> expiredCards = storedValueCardService.getExpiredCards();
        
        for (StoredValueCard card : expiredCards) {
            try {
                storedValueCardService.freezeExpiredGift(card.getId());
                logger.info("储值卡 {} 已过期，冻结赠送金额 {}", card.getId(), card.getRemainingGift());
            } catch (Exception e) {
                logger.error("处理储值卡 {} 过期时出错: {}", card.getId(), e.getMessage());
            }
        }
    }

    private void processExpiredPackageCards() {
        List<PackageCard> expiredCards = packageCardService.getExpiredCards();
        
        for (PackageCard card : expiredCards) {
            try {
                packageCardService.markAsExpired(card.getId());
                logger.info("套餐卡 {} 已过期，剩余次数 {}", card.getId(), card.getRemainingTimes());
            } catch (Exception e) {
                logger.error("处理套餐卡 {} 过期时出错: {}", card.getId(), e.getMessage());
            }
        }
    }
}
