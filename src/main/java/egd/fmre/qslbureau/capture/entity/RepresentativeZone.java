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

@Data
@Entity
@Table(name = "D_REPRESENTATIVE_ZONE")
public class RepresentativeZone implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDREPRESENTATIVEZONE")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDZONE")
    private Zone zone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDREPRESENTATIVE")
    private Representative representative;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCAPTURER")
    private Capturer capturer;

    @Column(name = "START")
    private Date start;

    @Column(name = "END")
    private Date end;   
}
