package com.clane.app.settings.kyc;

import com.clane.app.core.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KycLevelService implements CommandLineRunner {
    private final KycLevelRepository levelRepository;

    public KycLevelService(KycLevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    private boolean levelExists(KycLevel level) {
        return this.levelRepository.findByCode(level.getCode()).isPresent();
    }

    @Cacheable(value="kycLevel", key="#code")
    public KycLevel findByCode(LevelCode code) {
        return this.levelRepository.findByCode(code).orElseThrow(() -> new DataNotFoundException("KYC level with code " + code.name() + " not found"));
    }

    @Override
    public void run(String... args) {
        // populate default levels. this could also be carried out using scripts
        List <KycLevel> levels = new ArrayList<>();
        KycLevel one = KycLevel.builder()
                .code(LevelCode.LEVEL1)
                .name("Level One")
                .bankTrfLimit(new BigDecimal(10000))
                .walletTrfLimit(new BigDecimal(10000))
                .maxBalance(new BigDecimal(50000))
                .build();
        KycLevel two = KycLevel.builder()
                .code(LevelCode.LEVEL2)
                .name("Level Two")
                .bankTrfLimit(new BigDecimal(50000))
                .walletTrfLimit(new BigDecimal(50000))
                .maxBalance(new BigDecimal(200000))
                .build();
        KycLevel three = KycLevel.builder()
                .code(LevelCode.LEVEL3)
                .name("Level Three")
                .bankTrfLimit(new BigDecimal(5000000))
                .walletTrfLimit(new BigDecimal(5000000))
                .maxBalance(new BigDecimal(-1)) // unlimited
                .build();
        levels.add(one);
        levels.add(two);
        levels.add(three);

        levels.forEach(level -> {
            if (!levelExists(level)) { // check if level exists
                this.levelRepository.save(level);
            }
        });
    }
}
