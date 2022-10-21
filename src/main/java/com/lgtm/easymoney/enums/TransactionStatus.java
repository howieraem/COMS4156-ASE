package com.lgtm.easymoney.enums;

/**
 * pending -> transaction not executed yet, DB not safe to read/write
 * complete -> done, safe to access DB
 * denied -> mark a transaction as denied and thus can't be accepted/declined.
 */
public enum TransactionStatus {
    TRANS_PENDING,
    TRANS_COMPLETE,
    TRANS_DENIED,
    TRANS_FAILED,
    SELF_COMPLETE
}
