package com.lgtm.easymoney.models;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * transaction schema.
 */
@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
public class Transaction implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
