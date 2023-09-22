package egd.fmre.qslbureau.capture.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.service.ReportsService;

@RestController
@RequestMapping("reports")
public class ReportController {
	
	@Autowired ReportsService reportsService;
	
	@GetMapping
	public void distinctCallsigns() {
		@SuppressWarnings("unused")
		List<QslsReport> map = reportsService.gettingCallsignsMap();
	}
}
