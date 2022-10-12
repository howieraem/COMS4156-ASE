package com.lgtm.easymoney.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import com.lgtm.easymoney.enums.Category;

@Entity
@Table(name="user_transaction")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserTransaction implements Serializable {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @Embeddable
    public static class Key implements Serializable {
        @ManyToOne
        @JoinColumn(name = "tid")
        private Transfer transfer;

        @ManyToOne
        @JoinColumn(name = "uid")
        private User user;
    }

    @EmbeddedId
    private Key key;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Category category;

    @Column(nullable = true)
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date time;
}
