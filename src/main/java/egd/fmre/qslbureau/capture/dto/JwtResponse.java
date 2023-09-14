package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    @Getter
    private final String jwtToken;
    
    @Getter @Setter
    private int capturerId;
    
    @Getter @Setter
    private String capturerName;
    
    @Getter @Setter
    private String capturerLastName;
    
    @Getter @Setter
    private String capturerUsername;
    
    @Getter @Setter
    private Set<LocalDto> locals;

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    
    
}
