package egd.fmre.qslbureau.capture.dto.jsonburo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BuroDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7821027826760886048L;
	private String asociado;
	private String direccion;
	private String nota;
	private String pais;
	private Boolean cerrado;
	private List<PrefijoDto> prefijos;
}
