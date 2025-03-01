package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CapturedCallsign implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4504221035404094321L;

	public CapturedCallsign(String callsign, Date oldestdatetime) {
		super();
		this.callsign = callsign;
		this.oldestdatetime = oldestdatetime;
	}

	private String callsign;
	private Date oldestdatetime;
}
