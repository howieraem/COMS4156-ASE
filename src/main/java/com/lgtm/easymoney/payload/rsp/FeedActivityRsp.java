package com.lgtm.easymoney.payload.rsp;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.UserType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

/**
 * transaction response.
 */
@AllArgsConstructor
@Data
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

  @Override
  public int hashCode() {
    return Objects.hash(fromUid, toUid, lastUpdateTime);
  }

  @Override
  @Generated
  public boolean equals(Object o) {
    if (!(o instanceof FeedActivityRsp that)) {
      return false;
    }
    return fromUid.equals(that.getFromUid())
        && toUid.equals(that.getToUid())
        && lastUpdateTime.equals(that.getLastUpdateTime());
  }
}
