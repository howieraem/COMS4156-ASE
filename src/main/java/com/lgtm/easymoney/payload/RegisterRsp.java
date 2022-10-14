package com.lgtm.easymoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RegisterRsp {
    private Long uid;

    // TODO include token?
}
