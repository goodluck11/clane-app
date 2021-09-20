package com.clane.app.settings.kyc;

import com.clane.app.core.data.Active;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Table(name = "set_kyc")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Active
public class KycLevel extends com.clane.app.core.data.Data {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LevelCode code;
    private String name;
    private BigDecimal maxBalance;
    private BigDecimal walletTrfLimit;
    private BigDecimal bankTrfLimit;
}
