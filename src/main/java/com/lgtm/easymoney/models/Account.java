package com.lgtm.easymoney.models;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="account")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 17)
    private String number;

    @Column(nullable = false, length = 9)
    private String routingNumber;

    @OneToOne(mappedBy = "account")
    private User accountUser;
}
