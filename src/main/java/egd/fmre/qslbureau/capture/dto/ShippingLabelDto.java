package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShippingLabelDto implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 384053283792942274L;
	
	private boolean generated;
	private String error;
	private String filename;
}
