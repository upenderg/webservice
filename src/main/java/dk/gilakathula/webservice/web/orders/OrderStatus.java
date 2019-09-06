package dk.gilakathula.webservice.web.orders;

import org.springframework.http.HttpStatus;

public class OrderStatus {
    private Boolean success;
    private String message;
    private HttpStatus httpStatus;

    public OrderStatus(Boolean success, String message, HttpStatus httpStatus) {
        this.success = success;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
