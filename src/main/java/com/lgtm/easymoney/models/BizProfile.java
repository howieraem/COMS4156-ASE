package com.lgtm.easymoney.models;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * business profile schema.
 */
@Entity
@Table(name = "biz_profile")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BizProfile implements Serializable {
  @Id
  @OneToOne(cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "uid")
  private User bizUser;

  @Column(nullable = true)
  private String promotionText;

  // TODO add more columns if needed
}
