package com.lgtm.easymoney.payload;

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
public class FeedActivityRsp {
  private Long fromUid;
  private Long toUid;
  private String amount;
  private String desc;
  private Date lastUpdateTime;
  private String promoText;
}
