package com.clane.app.security.user;

import com.clane.app.core.data.Active;
import com.clane.app.core.data.BaseEntity;
import com.clane.app.security.user.dto.CreateUserDto;
import com.clane.app.settings.kyc.LevelCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Table(name = "sec_user")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Active
public class User extends BaseEntity {
    private String firstName;
    private String lastName;
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LevelCode levelCode;

    public static User mapDto(CreateUserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .levelCode(LevelCode.LEVEL1)
                .build();
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public String fullName() {
        return lastName + " " + firstName;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void encryptPassword() {
        PasswordEncoder ENCODER = new BCryptPasswordEncoder();
        this.password = ENCODER.encode(password);
    }
}
