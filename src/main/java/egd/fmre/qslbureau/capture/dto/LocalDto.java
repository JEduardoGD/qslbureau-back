package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class LocalDto implements Serializable {
    private static final long serialVersionUID = -7291106633844208645L;
    
    private int id;
    
    private int maxSlots;
    
    private String name;
    
}
