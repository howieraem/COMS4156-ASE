package com.lgtm.easymoney.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.lgtm.easymoney.configs.DbConsts;
import com.lgtm.easymoney.configs.ValidationConsts;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * account schema.
 */
@Entity
@Table(
        name = "account",
        uniqueConstraints = @UniqueConstraint(
                name = DbConsts.ACCOUNT_NUMBERS_CONSTRAINT,
                columnNames = {"accountNumber", "routingNumber"}
        )
)
/*
 * schema for bank account.
 * */
@Getter
@Setter
@NoArgsConstructor
public class Account implements Serializable {
  /*id of account
   * */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /*name of account, string*/
  @Column(nullable = false)
  private String accountName;
  /*account number, string*/
  @Column(nullable = false, length = ValidationConsts.MAX_ACCOUNT_NUMBER_LEN)
  private String accountNumber;
  /*routing number, string*/
  @Column(nullable = false, length = ValidationConsts.ROUTING_NUMBER_LEN)
  private String routingNumber;
  /*associated User, one-to-one mapping*/
  @OneToOne(mappedBy = "account")
  @JsonBackReference
  private User accountUser;

  /** Create a minimal account for unit test purpose. */
  public static Account ofTest(String accountName, String accountNumber) {
    var a = new Account();
    a.setAccountName(accountName);
    a.setAccountNumber(accountNumber);
    a.setRoutingNumber("000000000");
    return a;
  }
}
