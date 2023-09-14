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
import javax.persistence.Table;

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
