package egd.fmre.qslbureau.capture.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.OrphanCallsignReportObjectDTO;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import egd.fmre.qslbureau.capture.service.impl.RedirectListObjectRepresentative;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@RestController
@RequestMapping("reports")
public class ReportController {

    @Autowired ReportsService        reportsService;
    @Autowired RepresentativeService representativeService;

    private static final String POR_REPRESENTATIVE_REPORT_FILENAME = "by_representative";
    private static final String QSLS_CAPTURADAS_REPORT_FILENAME = "captured_qsls";
    private static final String ORPHANS_CALLSIGNS_REPORT_FILENAME = "callsign_huerfanos";
    private static final String REPORT_DATEFORMAT = "yyMMdd'_'HH";
    private static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";

    @GetMapping("/reporte-qsls-filename")
    public ResponseEntity<StandardResponse> getCapturedCalssignsReportFilenae() throws QslcaptureException {
	StandardResponse standardResponse = new StandardResponse(createFilename(QSLS_CAPTURADAS_REPORT_FILENAME));
	return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/reporte-qsls", produces = APPLICATION_VND_MS_EXCEL)
    public @ResponseBody ResponseEntity<byte[]> getCapturedCalssignsReport() throws IOException {
	List<CapturedCallsign> capturedCallsigns = reportsService.getReportOfCapturedCallsigns();
	byte[] resByteArray = reportsService.writeReportOfCapturedCallsigns(capturedCallsigns);

	return ResponseEntity.ok()
		.header(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=" + createFilename(QSLS_CAPTURADAS_REPORT_FILENAME))
		.contentLength(resByteArray.length).contentType(MediaType.parseMediaType(APPLICATION_VND_MS_EXCEL))
		.body(resByteArray);
    }

    @GetMapping("/reporte-redoreccopmes-filename/{representativeId}")
    public ResponseEntity<StandardResponse> bySlot(@PathVariable int representativeId) throws QslcaptureException {
	Representative representative = representativeService.getActiveRepresentativesById(representativeId);
	if (representative == null) {
	    return null;
	}
	StandardResponse standardResponse = new StandardResponse(createFilename(
		String.format("%s_%s", POR_REPRESENTATIVE_REPORT_FILENAME, representative.getUsername())));
	return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }
    

    @GetMapping(value = "/reporte-redoreccopmes/{representativeId}", produces = APPLICATION_VND_MS_EXCEL)
    public @ResponseBody ResponseEntity<byte[]> getRedireccionesReport(@PathVariable int representativeId)
	    throws IOException {

	Representative representative = representativeService.getActiveRepresentativesById(representativeId);
	if (representative == null) {
	    return null;
	}

	RedirectListObjectRepresentative redirectListObjectRepresentative = reportsService
		.redirectLists(representative);
	byte[] workbook = reportsService.redirectListObjectRepresentativeToWorkbook(redirectListObjectRepresentative);

	return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
		"attachment; filename=" + createFilename(
			String.format("%s_%s", POR_REPRESENTATIVE_REPORT_FILENAME, representative.getUsername())))
		.contentLength(workbook.length).contentType(MediaType.parseMediaType(APPLICATION_VND_MS_EXCEL))
		.body(workbook);
    }

    @GetMapping("/orphans-calls-report-filename")
    public ResponseEntity<StandardResponse> bySlot() throws QslcaptureException {
	StandardResponse standardResponse = new StandardResponse(createFilename(ORPHANS_CALLSIGNS_REPORT_FILENAME));
	return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/orphans-calls-report", produces = APPLICATION_VND_MS_EXCEL)
    public @ResponseBody ResponseEntity<byte[]> getOrphansCallsignsReport() throws IOException {
	List<OrphanCallsignReportObjectDTO> prphanCallsignReportObjectDTOList = reportsService
		.getOrphansCallsignsReport();
	byte[] reportInBytes = reportsService.generateOrphansReport(prphanCallsignReportObjectDTOList);

	return ResponseEntity.ok()
		.header(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=" + createFilename(ORPHANS_CALLSIGNS_REPORT_FILENAME))
		.contentLength(reportInBytes.length).contentType(MediaType.parseMediaType(APPLICATION_VND_MS_EXCEL))
		.body(reportInBytes);
    }

    private String createFilename(String staticPart) {
	return String.format("%s_%s.xlsx", staticPart,
		new SimpleDateFormat(REPORT_DATEFORMAT).format(DateTimeUtil.getDateTime()));
    }
}
