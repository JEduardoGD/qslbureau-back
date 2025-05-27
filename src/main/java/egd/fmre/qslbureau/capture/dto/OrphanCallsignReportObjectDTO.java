package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrphanCallsignReportObjectDTO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5708283036656053006L;
	private String representativeName;
	private String zoneName;
	private String qslTo;
	private String qslVia;
}
