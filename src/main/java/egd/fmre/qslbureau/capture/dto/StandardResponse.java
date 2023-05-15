package egd.fmre.qslbureau.capture.dto;

import lombok.Data;

@Data
public class StandardResponse {
    private boolean error;
    private String errorMessage;
    private String jsonPayload;
    
    public StandardResponse(String jsonPayload) {
        this.jsonPayload = jsonPayload;
        this.error = false;
    }
}
