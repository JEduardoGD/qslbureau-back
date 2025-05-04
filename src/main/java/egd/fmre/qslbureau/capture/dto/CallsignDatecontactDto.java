package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallsignDatecontactDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String callsign;
	private Integer slotId;
	private Date datetime;
}
