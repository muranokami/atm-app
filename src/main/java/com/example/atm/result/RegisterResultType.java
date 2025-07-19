package com.example.atm.result;

public enum RegisterResultType {
    SUCCESS("登録成功"),
    DUPLICATE_USERNAME("そのユーザーは既に使われています"),
    DUPLICATE_EMAIL("そのメールアドレスは既に登録されています"),
    WEAK_PASSWORD("パスワードの強度が不足しています"),
    INVALID_DOMAIN("無効なメールドメイン、または既に登録されています"),
    ERROR("登録処理でエラーが発生しました");
    
    private final String message;
    
    private RegisterResultType(String message) {
       this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
