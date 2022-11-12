package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.DbConsts;
import com.lgtm.easymoney.configs.ValidationConsts;
import com.lgtm.easymoney.enums.UserType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * user schema.
 */
@Entity
@Table(
        name = "user",
        uniqueConstraints = @UniqueConstraint(
                name = DbConsts.USER_EMAIL_CONSTRAINT,
                columnNames = {"email"})
)
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Email
  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(length = ValidationConsts.PHONE_LEN)
  private String phone;

  @Column
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(length = ValidationConsts.MAX_USER_TYPE_LEN, nullable = false)
  private UserType type;

  @Column(nullable = false)
  private BigDecimal balance = BigDecimal.ZERO;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "aid", referencedColumnName = "id")
  private Account account;

  @OneToOne(mappedBy = "bizUser", cascade = CascadeType.ALL)
  private BizProfile bizProfile;

  @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Friendship> friendships;

  @ManyToMany(mappedBy = "groupUsers", fetch = FetchType.LAZY)
  private Set<Group> groups;

  /**
   * transfer: sender is transaction.from, receiver is transaction.to
   * request: requestor is transaction.to, approver is transaction.from
   */
  @OneToMany(mappedBy = "from", fetch = FetchType.LAZY)
  private Set<Transaction> transactionsSent;
  @OneToMany(mappedBy = "to", fetch = FetchType.LAZY)
  private Set<Transaction> transactionsReceived;

  /**
   * set user type, either personal, business, or financial.
   *
   * @param userTypeStr user type, can be one of three types.
   */
  public void setTypeByStr(final String userTypeStr) {
    type = UserType.valueOf(userTypeStr.toUpperCase());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    User that = (User) obj;
    return Objects.equals(id, that.getId());
  }
}
