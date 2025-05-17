package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class MergeableDataDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6337414556007153700L;
	private SlotDto slotOrigen;
	private SlotDto slotDestino;
}
