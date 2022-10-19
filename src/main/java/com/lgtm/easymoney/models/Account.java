package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.DBConsts;
import java.io.Serializable;
import javax.persistence.*;

import com.lgtm.easymoney.configs.ValidationConsts;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "account",
        uniqueConstraints = @UniqueConstraint(name = DBConsts.ACCOUNT_NUMBERS_CONSTRAINT, columnNames = {"accountNumber", "routingNumber"})
)
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false, length = ValidationConsts.MAX_ACCOUNT_NUMBER_LEN)
    private String accountNumber;

    @Column(nullable = false, length = ValidationConsts.ROUTING_NUMBER_LEN)
    private String routingNumber;

    @OneToOne(mappedBy = "account")
    private User accountUser;
}
