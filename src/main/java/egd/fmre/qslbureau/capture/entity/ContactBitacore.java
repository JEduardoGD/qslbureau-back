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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "B_CONTACT")
public class ContactBitacore implements Serializable {
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
	private static final long serialVersionUID = -2049624780051989537L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IDBCONTACT")
	private Integer id;
	
	@Column(name = "DATETIME")
	private Date datetime;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDREPRESENTATIVE")
	private Representative Representative;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDCONTACTACT")
	private Contact contact;
}
