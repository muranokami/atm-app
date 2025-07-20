package com.example.atm.result;

/**
 * ユーザー登録処理の結果状態を表す列挙型
 * 各定数は登録処理の結果を示し、対応するメッセージを持つ
 */
public enum RegisterResultType {
    
    /** 登録成功 */
    SUCCESS("登録成功"),
    
    /** ユーザー名が既に使われている */
    DUPLICATE_USERNAME("そのユーザーは既に使われています"),
    
    /** メールアドレスが既に登録されている */
    DUPLICATE_EMAIL("そのメールアドレスは既に登録されています"),
    
    /** パスワードの強度不足 */
    WEAK_PASSWORD("パスワードの強度が不足しています"),
    
    /** 無効なメールドメイン、または既に登録されている */
    INVALID_DOMAIN("無効なメールドメイン、または既に登録されています"),
    
    /** 登録処理中に何らかのエラーが発生 */
    ERROR("登録処理でエラーが発生しました");
    
    /** 各結果に対応するメッセージ */
    private final String message;
    
    
    /**
     * コンストラクタ
     *  message 結果に対応する表示メッセージ
     */
    private RegisterResultType(String message) {
       this.message = message;
    }
    
    /**
     * 結果メッセージを取得する
     *  メッセージ文字列
     */
    public String getMessage() {
        return message;
    }
}
