package com.clane.app.core.data;

import com.clane.app.core.utils.Utils;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@lombok.Data
public class BaseEntity extends Data {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "code", nullable = false, unique = true, updatable = false)
    private String code;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @UpdateTimestamp
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Column(nullable = false)
    private Boolean status;
    @Version
    private Long version;

    @PrePersist
    public void setCode() {
        Active active = this.getClass().getDeclaredAnnotation(Active.class);
        this.setStatus(active != null && active.flag());
        this.setCode(Utils.hash(forCode()));
    }

    public String forCode() {
        return Utils.generateRandomString();
    }
}
