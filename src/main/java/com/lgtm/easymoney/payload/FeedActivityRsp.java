package com.lgtm.easymoney.payload;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
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
public class FeedActivityRsp {
  private Long fromUid;
  private Long toUid;
  private UserType fromType;
  private UserType toType;
  private Category category;
  private BigDecimal amount;
  private String desc;
  private Date lastUpdateTime;
  private String promoText;
}
