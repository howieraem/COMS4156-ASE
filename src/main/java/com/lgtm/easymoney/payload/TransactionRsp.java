package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransactionRsp {
    private Long fromUid;
    private Long toUid;
    private BigDecimal amount;
    private TransactionStatus status;
    private String desc;
    private Category category;
    private Date lastUpdateTime;
}
