package egd.fmre.qslbureau.capture.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.dto.EmailDataDto;
import egd.fmre.qslbureau.capture.dto.QslSumatoryDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.SendMailException;
import egd.fmre.qslbureau.capture.service.ContactBitacoreService;
import egd.fmre.qslbureau.capture.service.ContactService;
import egd.fmre.qslbureau.capture.service.EmailService;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("contact")
@Slf4j
public class ContactController {

	@Autowired ContactService         contactService;
    @Autowired RepresentativeService  representativeService;
    @Autowired SlotLogicService       slotLogicService;
    @Autowired EmailService           emailService;
    @Autowired QslService             qslService;
    @Autowired ContactBitacoreService contactBitacoreService;

	@GetMapping("/findactiveforcallsign/{callsign}")
	public ResponseEntity<StandardResponse> findActiveForCallsign(@PathVariable String callsign) {
		ContactDataDto contactDataDto = contactService.findActiveForCallsign(callsign);
		StandardResponse standardResponse = new StandardResponse(contactDataDto);
		return ResponseEntity.ok(standardResponse);
	}

	@GetMapping("/sendmail/slotid/{slotid}/representativeId/{representativeId}")
	public ResponseEntity<StandardResponse> sendMail(@PathVariable int slotid, @PathVariable int representativeId) {
		StandardResponse standardResponse;
		Representative representative = representativeService.findById(representativeId);
		if (representative == null) {
			standardResponse = new StandardResponse(true,
					String.format("Error con el representate id %d", representativeId));
			return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
		}
		Slot slot = slotLogicService.findById(slotid);
		List<Qsl> qsls = qslService.getActiveQslsForSlot(slot);
		List<QslSumatoryDto> qslsSumatory = new ArrayList<>();
		ContactDataDto contactData = contactService.findActiveForCallsign(slot.getCallsignto());
		if(contactData == null) {
			standardResponse = new StandardResponse(true, "No existen datos de contacto para el callsign");
			return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
		}
		for (Qsl qsl : qsls) {
			QslSumatoryDto qslSumatoryDto = new QslSumatoryDto();
			qslSumatoryDto.setLocalId(slot.getLocal().getId());
			qslSumatoryDto.setSlotNumber(slot.getSlotNumber());
			qslSumatoryDto.setToCallsign(qsl.getTo());
			qslSumatoryDto.setVia(qsl.getVia() != null ? qsl.getVia() : null);
			int index = qslsSumatory.indexOf(qslSumatoryDto);
			if (index > -1) {
				qslsSumatory.get(index).setC(qslsSumatory.get(index).getC() + 1);
			} else {
				qslSumatoryDto.setC(1);
				qslsSumatory.add(qslSumatoryDto);
			}
		}
		
		String grid = qslsSumatory.stream().map(q -> {
			StringBuffer sb = new StringBuffer();
			sb.append("<tr>");
			sb.append("<td>").append(q.getToCallsign()).append("</td>");
			sb.append("<td>").append(q.getVia() != null ? q.getVia() : "").append("</td>");
			sb.append("<td>").append(q.getC()).append("</td>");
			sb.append("</tr>");
			return sb.toString();
		}).collect(Collectors.joining());
		
		EmailDataDto emailDataDto = new EmailDataDto();
		emailDataDto.setEmailAddress(contactData.getEmail());
		emailDataDto.setNombre(contactData.getName());
		emailDataDto.setApellido(contactData.getSurename());
		emailDataDto.setIndicativo(contactData.getCallsign());
		emailDataDto.setGrid(grid);
		
		try {
			if(emailService.sendMail(emailDataDto)) {
				Contact contact = new Contact();
				contact.setId(contactData.getIdContact());
				ContactBitacore res = contactBitacoreService.saveContact(contact, representative);
				standardResponse = new StandardResponse(res);
				return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
			}else {
				log.error("Ocurrio un error al enviar correo");
				standardResponse = new StandardResponse(true,"Ocurrio un error al enviar correo");
				return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
			}
		} catch (SendMailException e) {
			log.error(e.getMessage());
			standardResponse = new StandardResponse(true, e.getMessage());
			return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
		}
	}
}
