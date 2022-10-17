package com.lgtm.easymoney.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InviteToGroupReq {
    @NotNull
    private Long gid;

    @NotNull
    private Long inviterId;

    @NotNull
    private Long inviteeId;
}
