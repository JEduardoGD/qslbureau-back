package egd.fmre.qslbureau.capture.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.MergeableDataDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.impl.RedirectListObjectRepresentative;

@RestController
@RequestMapping("aplicablerules")
public class RulesController {

	@Autowired CallsignRuleService callsignRuleService;
	@Autowired SlotLogicService slotLogicService;
	@Autowired LocalService localService;
	@Autowired RepresentativeService representativeService;
	@Autowired ReportsService        reportsService;

	@GetMapping("/{localid}")
	public ResponseEntity<StandardResponse> getApplicableRules(@PathVariable int localid) throws QslcaptureException {
		List<MergeableDataDto> mergeableDataDtoList = slotLogicService.getApplicableTraslates(localid);
		StandardResponse standardResponse;
		standardResponse = new StandardResponse(mergeableDataDtoList);
		return new ResponseEntity<StandardResponse>(standardResponse, HttpStatus.OK);
	}

	@PutMapping("applyrules")
	public ResponseEntity<StandardResponse> applyRules(@RequestBody MergeableDataDto mergeableDataDto,
			@RequestHeader Map<String, String> headers) {
		boolean result = slotLogicService.mergeChanges(mergeableDataDto);
		StandardResponse standardResponse;
		standardResponse = new StandardResponse(result);
		return new ResponseEntity<StandardResponse>(standardResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/reporte-redoreccopmes")
	public ResponseEntity<StandardResponse> getAllRedireccionesReport() throws IOException {

		List<Representative> representatives = representativeService.findAllActive();
		if (representatives == null || representatives.isEmpty()) {
			return null;
		}

		List<RedirectListObjectRepresentative> redirectListObjectRepresentativeList = representatives.stream()
				.map(r -> reportsService.redirectLists(r)).collect(Collectors.toList());

		StandardResponse standardResponse;
		standardResponse = new StandardResponse(redirectListObjectRepresentativeList);
		return new ResponseEntity<StandardResponse>(standardResponse, HttpStatus.OK);

	}
}
