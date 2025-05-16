package egd.fmre.qslbureau.capture.controller;

import java.util.List;
import java.util.Map;

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
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;

@RestController
@RequestMapping("aplicablerules")
public class RulesController {

	@Autowired
	CallsignRuleService callsignRuleService;
	@Autowired
	SlotLogicService slotLogicService;
	@Autowired
	LocalService localService;

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
}
