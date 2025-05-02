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
import lombok.ToString;

@Data
@Entity
@Table(name = "T_CONTACT")
@ToString
public class Contact implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -2955057386123613669L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCONTACTACT")
    private Integer id;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "SURENAME")
    private String surename;
    
    @Column(name = "CALLSIGN")
    private String callsign;
    
    @Column(name = "ADDRESS")
    private String address;
    
    @Column(name = "EMAIL")
    private String email;
    
    @Column(name = "WHATSAPP")
    private String whatsapp;
    
    @Column(name = "WANTEMAIL")
    private Boolean wantemail;
    
    @Column(name = "START")
    private Date start;
    
    @Column(name = "END")
    private Date end;
}
