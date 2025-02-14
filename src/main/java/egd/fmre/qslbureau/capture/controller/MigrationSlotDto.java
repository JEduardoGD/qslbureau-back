package egd.fmre.qslbureau.capture.controller;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class MigrationSlotDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8575251732709267984L;
	private Integer slotid;
	private Integer newlocalid;
}
