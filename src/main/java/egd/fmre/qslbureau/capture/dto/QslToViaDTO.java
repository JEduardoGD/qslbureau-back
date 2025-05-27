package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class QslToViaDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3156009687046813413L;
	private String to;
	private String via;
}
