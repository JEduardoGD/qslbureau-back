package egd.fmre.qslbureau.capture.dto.jsonburo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BuroNodoPrincipalDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8589488228335614167L;
	private List<BuroDto> buroes;
}
