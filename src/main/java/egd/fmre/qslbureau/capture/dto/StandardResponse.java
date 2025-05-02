package egd.fmre.qslbureau.capture.dto;

import lombok.Data;

@Data
public class StandardResponse {
    private boolean error;
    private String errorMessage;
    private String jsonPayload;
    private Object objectPayload;
    
    public StandardResponse(String jsonPayload) {
        this.jsonPayload = jsonPayload;
        this.error = false;
    }
    
    public StandardResponse(boolean error, String errorMessage) {
        this.error = error;
        this.errorMessage = errorMessage;
    }
    
    public StandardResponse(Object objectPaypload) {
        this.objectPayload = objectPaypload;
    }
}
