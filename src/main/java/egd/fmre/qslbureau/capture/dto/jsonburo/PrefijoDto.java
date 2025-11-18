package egd.fmre.qslbureau.capture.dto.jsonburo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PrefijoDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 321919265743098952L;
	@JsonProperty("inicial")
	private String inicio;
	@JsonProperty("final")
	private String fin;
}
