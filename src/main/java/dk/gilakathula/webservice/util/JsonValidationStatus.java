package dk.gilakathula.webservice.util;

public class JsonValidationStatus {
    private Boolean success;
    private String message;

    private JsonValidationStatus(Builder builder) {
        this.success = builder.status;
        this.message = builder.message;
    }

    public static Builder newJsonValidationStatus() {
        return new Builder();
    }

    public Boolean isValid() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static final class Builder {
        private Boolean status;
        private String message;

        private Builder() {
        }

        public JsonValidationStatus build() {
            return new JsonValidationStatus(this);
        }

        public Builder status(Boolean status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }
    }
}
