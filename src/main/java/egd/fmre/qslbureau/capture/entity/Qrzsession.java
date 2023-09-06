package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "T_QRZSESSION")
@EqualsAndHashCode(of = { "id" })
public class Qrzsession implements Serializable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final long serialVersionUID = 2463189018733802601L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDQRZSESSION")
    private Integer id;

    @Column(name = "S_KEY")
    private String key;

    @Column(name = "N_COUNT")
    private Integer count;

    @Column(name = "D_SUB_EXP")
    private Date subExp;

    @Column(name = "D_GM_TIME")
    private Date gmTime;

    @Column(name = "S_REMARK")
    private String remark;

    @Column(name = "S_ERROR")
    private String error;
}
