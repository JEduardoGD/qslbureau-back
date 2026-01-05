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
@Table(name = "C_REPRESENTATIVE")
public class Representative implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4921645362017391197L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDREPRESENTATIVE")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "START")
    private Date start;
    
    @Column(name = "END")
    private Date end;
    
    @OneToOne(mappedBy = "representative")
    private RepresentativeZone representativeZone;
}
