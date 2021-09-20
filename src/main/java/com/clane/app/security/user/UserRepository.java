package com.clane.app.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNo);
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNo);
}
