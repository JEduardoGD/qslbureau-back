package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "C_CAPTURER")
public class Capturer implements Serializable {
    private static final long serialVersionUID = 1671542222670208320L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCAPTURER")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LASTNAME")
    private String lastName;
}
