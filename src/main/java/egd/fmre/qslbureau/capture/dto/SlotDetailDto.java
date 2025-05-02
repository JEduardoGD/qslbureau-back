package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SlotDetailDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7170163426680652506L;

	private List<QslSumatoryDto> qslSumatoryList;
	private SlotDto SlotDto;
	
}
