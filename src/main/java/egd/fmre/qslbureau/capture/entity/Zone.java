package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "C_ZONE")
public class Zone implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7311724402733168969L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDZONE")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "START")
    private Date start;

    @Column(name = "END")
    private Date end;
    
    /*
    @OneToMany(mappedBy = "zone")
    private List<Zonerule> zonerules;
    */
    
    @OneToOne(mappedBy = "zone")
    private RepresentativeZone representativeZone;

}
