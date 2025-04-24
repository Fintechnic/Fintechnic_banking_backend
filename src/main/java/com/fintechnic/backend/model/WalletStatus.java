package com.fintechnic.backend.model;

public enum WalletStatus {
    ACTIVE, // ví đang hoạt động, có thể giao dịch
    INACTIVE, // ví không hoạt động, tạm thời không sử dụng được
    SUSPENDED, // ví bị tạm ngưng hoạt động vì lý do nào đó
    CLOSED // ví bị đóng vĩnh viễn
}
