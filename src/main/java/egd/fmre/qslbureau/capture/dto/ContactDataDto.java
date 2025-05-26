package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ContactDataDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4657311787547954701L;

	private Integer idContact;
	private String name;
	private String surename;
	private String callsign;
	private String address;
	private String email;
	private String whatsapp;
	private Boolean wantemail;
	private Date start;
	private Date end;
	//private String listOf;
}
