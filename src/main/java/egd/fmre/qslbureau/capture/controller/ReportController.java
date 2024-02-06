package egd.fmre.qslbureau.capture.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.SlotReport;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.util.ReportUtil;

@RestController
@RequestMapping("reports")
public class ReportController {

	@Autowired
	ReportsService reportsService;

	@GetMapping
	public void distinctCallsigns() {
		@SuppressWarnings("unused")
		List<QslsReport> map = reportsService.gettingCallsignsMap();

	}

	@GetMapping(value = "/get-image-with-media-type", produces = "application/vnd.ms-excel")
	public @ResponseBody byte[] getImageWithMediaType() throws IOException {
		List<QslsReport> map = reportsService.gettingCallsignsMap();
		return ReportUtil.writeReport(map);
	}

	@GetMapping(value = "/qslsbycapturer/{capturerid}", produces = "application/vnd.ms-excel")
	public @ResponseBody byte[] getQslsByCapturer(@PathVariable int capturerid) throws IOException {
		List<SlotReport> map = reportsService.getQslsByCapturer(capturerid);
		return ReportUtil.writeSlotsReport(map);
	}

}
