package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RepresentativeInfo implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6320273229123189879L;

	private Long representativeId;
	private String representativeName;
	private String zone;
	private String callsign;

}
