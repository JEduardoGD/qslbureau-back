package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "T_QRZREG")
@EqualsAndHashCode(of = { "id" })
public class Qrzreg implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 5825179921964483323L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDQRZREG")
    private Integer id;

    @Column(name = "S_CALLSIGN")
    private String callsign;

    @Column(name = "I_DXCC")
    private Long dxcc;

    @Column(name = "S_STATE")
    private String state;

    @Column(name = "I_ITU")
    private Integer itu;

    @Column(name = "I_CQ")
    private Integer cq;

    @Column(name = "S_COUNTRY")
    private String country;

    @Column(name = "D_UPDATED_AT")
    private Date updatedAt;
}
