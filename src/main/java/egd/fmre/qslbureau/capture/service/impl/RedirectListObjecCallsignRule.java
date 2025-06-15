package egd.fmre.qslbureau.capture.service.impl;

import java.io.Serializable;

import lombok.Data;

@Data
public class RedirectListObjecCallsignRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6797938981359235793L;

	private String callsignTo;
	private String callsignRedirect;
	private Integer localId;
	private Integer slotNum;
	private Integer total;
}
