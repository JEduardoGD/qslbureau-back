package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("capturerId")
    @JoinColumn(name = "IDCAPTURER")
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
