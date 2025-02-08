package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "C_ZONERULE")
public class Zonerule implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081936013467456964L;
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDZONERULE")
    private Integer id;
    private Capturer capturer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDZONE")
    private Zone zone;

    @Column(name = "CALLSIGN")
    private String callsign;

    @Column(name = "START")
    private Date start;

    @Column(name = "END")
    private Date end;
}
