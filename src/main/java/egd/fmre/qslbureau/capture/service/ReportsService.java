package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.OrphanCallsignReportObjectDTO;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.service.impl.RedirectListObjectRepresentative;

public interface ReportsService {

	List<QslsReport> gettingCallsignsMap();

	byte[] writeReportOfCapturedCallsigns(List<CapturedCallsign> captiredCallsigns);

	List<CapturedCallsign> getReportOfCapturedCallsigns();

	RedirectListObjectRepresentative redirectLists(Representative representative);

	byte[] redirectListObjectRepresentativeToWorkbook(
			RedirectListObjectRepresentative redirectListObjectRepresentative);

	List<OrphanCallsignReportObjectDTO> getOrphansCallsignsReport();

	byte[] generateOrphansReport(List<OrphanCallsignReportObjectDTO> orphanCallsignReportObjectDTOList);
}
