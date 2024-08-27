package com.app.rupyz.generic.base;

public class FailureResponse {

    private int errorCode;
    private CharSequence errorMessage;
    private String errorBody;

    public FailureResponse() {
    }

    public FailureResponse(int errorCode, CharSequence errorMessage, String errorBody) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorBody = errorBody;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

    public CharSequence getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
