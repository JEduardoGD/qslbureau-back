package egd.fmre.qslbureau.capture.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.impl.RedirectListObjectRepresentative;

@RestController
@RequestMapping("reports")
public class ReportController {

	@Autowired ReportsService        reportsService;
	@Autowired RepresentativeService representativeService;

	@GetMapping
	public void distinctCallsigns() {
		@SuppressWarnings("unused")
		List<QslsReport> map = reportsService.gettingCallsignsMap();

	}

	@GetMapping(value = "/get-image-with-media-type", produces = "application/vnd.ms-excel")
	public @ResponseBody ResponseEntity<byte[]> getImageWithMediaType() throws IOException {
		List<QslsReport> map = reportsService.gettingCallsignsMap();
		 byte[] resByteArray = reportsService.writeReport(map);
		
		return ResponseEntity.ok()
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"filename.xlsx")
		        .contentLength(resByteArray.length)
		        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		        .body(resByteArray);
	}

	@GetMapping(value = "/reporte-qsls", produces = "application/vnd.ms-excel")
	public @ResponseBody ResponseEntity<byte[]> getCapturedCalssignsReport() throws IOException {
		 List<CapturedCallsign> capturedCallsigns = reportsService.getReportOfCapturedCallsigns();
		 byte[] resByteArray = reportsService.writeReportOfCapturedCallsigns(capturedCallsigns);
		
		return ResponseEntity.ok()
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"filename.xlsx")
		        .contentLength(resByteArray.length)
		        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		        .body(resByteArray);
	}

	@GetMapping(value = "/reporte-redoreccopmes/{representativeId}", produces = "application/vnd.ms-excel")
	public @ResponseBody ResponseEntity<byte[]> getRedireccionesReport(@PathVariable int representativeId) throws IOException {
		
		Representative representative = representativeService.getActiveRepresentativesById(representativeId);
		if(representative == null) {
			return null;
		}
		
		RedirectListObjectRepresentative redirectListObjectRepresentative = reportsService.redirectLists(representative);
		byte[] workbook = reportsService.redirectListObjectRepresentativeToWorkbook(redirectListObjectRepresentative);
		
		return ResponseEntity.ok()
		        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+"filename.xlsx")
		        .contentLength(workbook.length)
		        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		        .body(workbook);
	}

	/*
	@GetMapping(value = "/orphans-calls-report", produces = "application/vnd.ms-excel")
	public @ResponseBody ResponseEntity<byte[]> generateOrphanCallsReport() throws IOException {
		 reportsService.getOrphansCallsigns();
		return null;
	}
	*/
}
