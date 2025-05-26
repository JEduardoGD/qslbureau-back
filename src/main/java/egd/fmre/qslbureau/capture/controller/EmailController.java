package egd.fmre.qslbureau.capture.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.CallsignDatecontactDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.service.ContactBitacoreService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("email")
public class EmailController {

	private SlotLogicService       slotLogicService;
	private ContactBitacoreService contactBitacoreService;

	@GetMapping("/getemailsendedforslot/{slotId}")
	public ResponseEntity<StandardResponse> getListOfEmailSendedForSlot(@PathVariable int slotId) {
		if (slotId <= 0) {
			return new ResponseEntity<StandardResponse>(new StandardResponse(true, "El slotId no puede ser cero"),
					new HttpHeaders(), HttpStatus.OK);
		}
		Slot slot = slotLogicService.findById(slotId);
		if (slot == null) {
			return new ResponseEntity<StandardResponse>(new StandardResponse(true, "Slot no encontrado"),
					new HttpHeaders(), HttpStatus.OK);
		}
		List<CallsignDatecontactDto> callsignDatecontactDtoList = contactBitacoreService.findBySlot(slot);
		StandardResponse standardResponse = new StandardResponse(callsignDatecontactDtoList);
		return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
	}
}
