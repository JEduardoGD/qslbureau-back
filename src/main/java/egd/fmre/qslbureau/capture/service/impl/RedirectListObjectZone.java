package egd.fmre.qslbureau.capture.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RedirectListObjectZone implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7368236002728816092L;

	private String name;
	private List<RedirectListObjecCallsignRule> rlocList;

	public RedirectListObjectZone() {
		this.rlocList = new ArrayList<>();
	}

}
