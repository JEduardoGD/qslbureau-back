package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.SlotReport;

public interface ReportsService {

	List<QslsReport> gettingCallsignsMap();

	List<SlotReport> getQslsByCapturer(int idCapturer);
}
