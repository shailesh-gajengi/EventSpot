package com.eventhosting.dto.response;

import java.io.Serializable;

/**
 * Standard API Response wrapper.
 * Encapsulates the response into three distinct parts:
 * - errorCode: numeric indicator (0 for success, non-zero for error/HTTP status code)
 * - errorMsg: descriptive status or error message
 * - body: payload data of generic type T
 *
 * @param <T> the type of the payload in body
 */
public class ResponseBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int errorCode;
    private String errorMsg;
    private T body;

    public ResponseBean() {
    }

    public ResponseBean(int errorCode, String errorMsg, T body) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.body = body;
    }

    public static <T> ResponseBean<T> success(T body) {
        return new ResponseBean<>(0, "Success", body);
    }

    public static <T> ResponseBean<T> success(String message, T body) {
        return new ResponseBean<>(0, message, body);
    }

    public static <T> ResponseBean<T> error(int errorCode, String errorMsg) {
        return new ResponseBean<>(errorCode, errorMsg, null);
    }

    public static <T> ResponseBean<T> error(int errorCode, String errorMsg, T body) {
        return new ResponseBean<>(errorCode, errorMsg, body);
    }

    public static <T> ResponseBean<T> error(String errorMsg) {
        return new ResponseBean<>(1, errorMsg, null);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", body=" + body +
                '}';
    }
}
