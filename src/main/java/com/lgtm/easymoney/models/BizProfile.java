package com.lgtm.easymoney.models;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="biz_profile")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BizProfile implements Serializable {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name ="uid")
    private User bizUser;
    
    @Column(nullable = true)
    private String promotionText;

    // TODO add more columns if needed
}
