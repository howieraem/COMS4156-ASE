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
import com.lgtm.easymoney.enums.MoneyRequestStatus;

@Entity
@Table(name="money_request")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MoneyRequest implements Serializable {
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

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date time;

    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = false)
    private MoneyRequestStatus status;
}
