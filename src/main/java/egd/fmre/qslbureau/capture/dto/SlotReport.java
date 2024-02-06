package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4529515925067647670L;
	private Slot slot;
	private Capturer capturer;
	private int qslsQuantity;
}
