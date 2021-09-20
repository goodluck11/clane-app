package com.clane.app.security.user;

import com.clane.app.security.user.dto.CreateUserDto;
import com.clane.app.wallet.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private WalletService walletService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User newUser(CreateUserDto userDto) {
        log.info("Creating user {}", userDto.toString());
        User user = User.mapDto(userDto);
        user.encryptPassword(); // hash user password
        User saved = userRepository.save(user); // create user model
        walletService.createWallet(saved); // create user wallet
        log.info("User successfully created with wallet....");
        // you can go ahead and authenticate user but for test purpose, I will just return the created user
        return saved;
    }

    @Cacheable(value="user", key="#userRef")
    public Optional<User> findByEmailOrPhoneNumber(String userRef) {
        return this.userRepository.findByEmailOrPhoneNumber(userRef, userRef);
    }

}
