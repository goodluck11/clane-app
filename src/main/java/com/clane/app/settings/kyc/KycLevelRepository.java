package com.clane.app.settings.kyc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycLevelRepository extends JpaRepository<KycLevel, Long> {
    Optional<KycLevel> findByCode(LevelCode code);
}
