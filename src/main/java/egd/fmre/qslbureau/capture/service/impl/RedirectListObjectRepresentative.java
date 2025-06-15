package egd.fmre.qslbureau.capture.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RedirectListObjectRepresentative implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398636004605908090L;
	private String name;
	private Integer representativeId;
	private List<RedirectListObjectZone> rlozList;

	public RedirectListObjectRepresentative() {
		this.rlozList = new ArrayList<>();
	}
}
