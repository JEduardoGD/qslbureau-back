package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.dto.QslsReport;

public interface ReportsService {

	List<QslsReport> gettingCallsignsMap();

	byte[] writeReport(List<QslsReport> list);

	byte[] writeReportOfCapturedCallsigns(List<CapturedCallsign> captiredCallsigns);

	List<CapturedCallsign> getReportOfCapturedCallsigns();

}
