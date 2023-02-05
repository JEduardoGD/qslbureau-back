package egd.fmre.qslbureau.capture.controller;

import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dao.Qsl;
import egd.fmre.qslbureau.capture.service.QslCaptureService;

@RestController
public class QslcapturaController {
	
	private QslCaptureService qslCaptureService;
	
	public Qsl captureQsl(Qsl qsl) {
		return qslCaptureService.captureQsl(qsl);
	}
}
