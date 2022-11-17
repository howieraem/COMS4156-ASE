package com.lgtm.easymoney.payload.req;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * request for creating a group.
 */
@Data
public class CreateGroupReq {
  @NotEmpty
  private List<@NotNull Long> uids;

  @NotBlank
  private String name;

  private String description;
}
