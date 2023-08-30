package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LocalDto implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = -7291106633844208645L;
    
    private int id;
    
    private int maxSlots;
    
    private String name;
    
}
