package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmailDataDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2328033728995421924L;
	private String emailAddress;
	private String nombre;
	private String apellido;
	private String indicativo;
	private String grid;
	private int numOfContact;
}
