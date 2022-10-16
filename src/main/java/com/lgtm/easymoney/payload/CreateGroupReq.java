package com.lgtm.easymoney.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateGroupReq {
    @NotEmpty
    private List<@NotNull Long> uids;

    @NotBlank
    private String name;

    private String description;
}
