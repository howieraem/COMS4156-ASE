package com.lgtm.easymoney.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * friendship schema.
 */
@Entity
@Table(name = "friendship")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Friendship implements Serializable {
  /**
   * 2 user id forms a composite key for friendship.
   */
  @Embeddable
  public static class Key implements Serializable {
    private Long uid1;
    private Long uid2;
  }

  @EmbeddedId
  private Key key = new Key();

  @ManyToOne
  @MapsId("uid1")
  private User user1;

  @ManyToOne
  @MapsId("uid2")
  private User user2;

  @Column(nullable = false)
  private Boolean active = false;

  @Column
  private String note;  // like alias

  public String getKeyString() {
    // this.key.toString() is not very informative
    return String.format("(%d,%d)", this.key.uid1, this.key.uid2);
  }
}