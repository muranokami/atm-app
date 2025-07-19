package com.example.atm.result;

public class RegisterResult {
    private final boolean success;
    private final RegisterResultType resultType;
    
    public RegisterResult(boolean success, RegisterResultType resultType) {
        this.success = success;
        this.resultType = resultType;
    }
    
    public boolean isSuccess() {
        return success;
    }
     
    public RegisterResultType getResultType() {
        return resultType;
    }

}
