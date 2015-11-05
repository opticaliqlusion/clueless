package edu.jhu.epioneers.clueless.communication;

/**
 * Generic response type that will be returned by all service operations
 * @param <T> Type of data object being returned
 */
public class Response<T> {
    /**
     * HTTP status code of the repsonse
     */
    private int httpStatusCode;

    /**
     * Additional status code returned by the server.  Optional and defaults to 0.
     */
    private int additionalStatusCode;

    /**
     * Additional status message returned by the server. Optional and defaults to null.
     */
    private String message;

    /**
     * Generic data object returned by the server
     */
    private T data;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getAdditionalStatusCode() {
        return additionalStatusCode;
    }

    public void setAdditionalStatusCode(int additionalStatusCode) {
        this.additionalStatusCode = additionalStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
