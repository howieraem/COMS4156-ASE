package com.lgtm.easymoney.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LeaveGroupReq {
    @NotNull
    private Long uid;

    @NotNull
    private Long gid;
}
