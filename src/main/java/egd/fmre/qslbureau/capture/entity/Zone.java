package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

}
