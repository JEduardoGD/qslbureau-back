package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "C_LOCAL")
public class Local implements Serializable {
    
    private static final long serialVersionUID = -4700192066610543036L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDLOCAL")
    private Integer id;
    
    @Column(name = "MAXSLOTS")
    private int maxSlots;
}
