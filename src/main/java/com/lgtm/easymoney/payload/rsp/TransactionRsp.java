package com.lgtm.easymoney.payload.rsp;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * transaction response.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransactionRsp {
  private Long fromUid;
  private Long toUid;
  private Long transactionId;
  private BigDecimal amount;
  private TransactionStatus status;
  private String desc;
  private Category category;
  private Date lastUpdateTime;
}
