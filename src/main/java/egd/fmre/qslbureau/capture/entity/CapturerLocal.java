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
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "D_CAPTURER_LOCAL")
@EqualsAndHashCode(of = {"id"})
public class CapturerLocal implements Serializable {

    private static final long serialVersionUID = -7843759781324034869L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCAPTURERLOCAL")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCAPTURER")
    private Capturer capturer;

    @ManyToOne(fetch = FetchType.LAZY)
    //@MapsId("localId")
    @JoinColumn(name = "IDLOCAL")
    private Local local;

    @Column(name = "START")
    private Date start;

    @Column(name = "END")
    private Date end;
    
    
}
