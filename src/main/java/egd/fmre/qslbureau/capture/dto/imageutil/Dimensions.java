package egd.fmre.qslbureau.capture.dto.imageutil;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dimensions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6276937413937540166L;
	private int width;
	private int height;
}
