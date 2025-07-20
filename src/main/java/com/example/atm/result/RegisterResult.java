package com.example.atm.result;

/**
 * ユーザー登録処理の結果を表すクラス
 * 成功・失敗の状態と、失敗の場合の理由を保持する
 */
public class RegisterResult {
    
    /** 登録処理が成功したかどうか */
    private final boolean success;
    
    /** 登録処理の結果タイプ（成功・重複・エラーなど） */
    private final RegisterResultType resultType;
    
    /**
    * コンストラクタ
    * success 成功ならtrue、失敗ならfalse
    * resultType 結果の詳細タイプ
    */
    public RegisterResult(boolean success, RegisterResultType resultType) {
        this.success = success;
        this.resultType = resultType;
    }
    
    /**
     * 登録成功判定
     * 成功ならtrue、失敗ならfalse
     */
    public boolean isSuccess() {
        return success;
    }
     
    /**
     * 結果の詳細タイプを取得
     */
    public RegisterResultType getResultType() {
        return resultType;
    }

}
