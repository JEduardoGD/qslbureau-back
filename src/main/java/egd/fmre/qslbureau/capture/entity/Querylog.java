package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
