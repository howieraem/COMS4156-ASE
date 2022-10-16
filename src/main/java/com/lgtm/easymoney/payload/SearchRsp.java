package com.lgtm.easymoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SearchRsp {
    private boolean success;
    private String accountName;
    private String email;
    private String address;
}
