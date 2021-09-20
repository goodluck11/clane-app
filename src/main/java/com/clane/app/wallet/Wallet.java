package com.clane.app.wallet;

import com.clane.app.core.data.Active;
import com.clane.app.security.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Table(name = "clane_wallet")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Active
public class Wallet extends com.clane.app.core.data.Data {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(precision = 31, scale = 2)
    private BigDecimal availableBalance;
    @Column(precision = 31, scale = 2)
    private BigDecimal totalBalance;
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "wallet_no", referencedColumnName = "phoneNumber")
    private User user;

}
