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
@Table(name = "C_CALLSIGNRULE")
public class CallsignRule implements Serializable {
    private static final long serialVersionUID = -5553927775606824170L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCALLSIGNRULE")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDCAPTURER")
    private Capturer capturer;

    @Column(name = "CALLSIGNTO")
    private String callsignTo;

    @Column(name = "CALLSIGNREDIRECT")
    private String callsignRedirect;

    @Column(name = "START")
    private Date start;

    @Column(name = "END")
    private Date end;
}
