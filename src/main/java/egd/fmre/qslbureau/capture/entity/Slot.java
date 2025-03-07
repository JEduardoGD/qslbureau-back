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
@Table(name = "T_SLOT")
public class Slot implements Serializable {

    private static final long serialVersionUID = -8116369935021235688L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSLOT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDLOCAL")
    private Local local;
    
    @Column(name = "CALLSIGNTO")
    private String callsignto;

    @Column(name = "SLOTNUMBER")
    private int slotNumber;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "CLOSED_AT")
    private Date closedAt;

    @Column(name = "SENT_AT")
    private Date sendAt;

    @Column(name = "MOVED_TO_INT_AT")
    private Date movedToIntAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="IDSTATUS")
    private Status status;

    @Column(name = "CONFIRM_CODE")
    private String confirmCode;
}
