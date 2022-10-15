package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.DBConsts;
import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name="account",
        uniqueConstraints=@UniqueConstraint(name= DBConsts.ACCOUNT_NUMBERS_CONSTRAINT, columnNames={"accountNumber", "routingNumber"})
)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false, length = 17)
    private String accountNumber;

    @Column(nullable = false, length = 9)
    private String routingNumber;

    @OneToOne(mappedBy = "account")
    private User accountUser;
}
