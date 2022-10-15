package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.DBConsts;
import com.lgtm.easymoney.enums.UserType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name="user",
        uniqueConstraints=@UniqueConstraint(name= DBConsts.USER_EMAIL_CONSTRAINT, columnNames={"email"})
)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true, length = 10)
    private String phone;

    @Column(nullable = true)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 9, nullable = false)
    private UserType type;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aid", referencedColumnName = "id")
    private Account account;

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

    @OneToOne(mappedBy = "bizUser", optional = true, cascade = CascadeType.ALL)
    private BizProfile bizProfile;

    public void setTypeByStr(String userTypeStr) {
        userTypeStr = userTypeStr.toLowerCase();
        if (userTypeStr.equals("financial")) {
            type = UserType.FINANCIAL;
        } else if (userTypeStr.equals("business")) {
            type = UserType.BUSINESS;
        } else {
            type = UserType.PERSONAL;
        }
    }
}
