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
@Table(name = "T_QUERYLOG")
public class Querylog implements Serializable {

    private static final long serialVersionUID = -5481284675001949065L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDQUERYLOG")
    private Integer id;

    @Column(name =  "Q")
    private String query;

    @Column(name =  "IP")
    private String ip;

    @Column(name =  "DATETIME")
    private Date datetime;

}
