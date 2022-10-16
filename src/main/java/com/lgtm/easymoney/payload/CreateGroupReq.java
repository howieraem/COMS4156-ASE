package com.lgtm.easymoney.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateGroupReq {
    @NotNull
    private List<Long> uids;

    @NotBlank
    private String name;

    private String description;
}
