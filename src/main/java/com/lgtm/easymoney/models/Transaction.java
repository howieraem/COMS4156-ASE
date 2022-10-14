package com.lgtm.easymoney.models;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="transaction")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_uid")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_uid")
    private User to;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Category category;

    @Column(nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TransactionStatus status;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastUpdateTime;
}
