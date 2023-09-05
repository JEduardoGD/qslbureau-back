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
@Table(name = "CAT_DXCC_SESSION")
public class DxccSession implements Serializable {
    private static final long serialVersionUID = 3672262009330432618L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_ID_DXCC_SESSION")
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
