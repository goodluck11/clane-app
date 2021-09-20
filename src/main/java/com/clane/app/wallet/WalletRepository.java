package com.clane.app.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser_EmailAndUser_PhoneNumber(String email, String phoneNo);
}
