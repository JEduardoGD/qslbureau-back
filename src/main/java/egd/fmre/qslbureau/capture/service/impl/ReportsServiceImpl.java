package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsServiceImpl implements ReportsService {

	@Autowired
	private SlotLogicService slotLogicService;
	@Autowired
	private QslCaptureService qslCaptureService;

	@Override
	public List<QslsReport> gettingCallsignsMap() {
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		List<QslsReport> list = new ArrayList<>();
		for (Slot slot : openedOrCreatedSlots) {
			try {
				Set<QslDto> qsls = qslCaptureService.qslsBySlot(slot.getId());
				for (QslDto qsl : qsls) {
					processCallsign(list, new QslsReportKey(qsl.getTo(), qsl.getVia()), qsl.getDateTimeCapture());
				}
			} catch (QslcaptureException e) {
				log.error(e.getMessage());
			}
		}
		return list;
	}

	private void processCallsign(List<QslsReport> list, QslsReportKey key, Date dateTimeCapture) {
		QslsReport qslsReport;
		int i = list.indexOf(new QslsReport(key));
		if (i == -1) {
			qslsReport = new QslsReport(key);
			qslsReport.setCount(0);
			list.add(qslsReport);
			i = list.indexOf(new QslsReport(key));
		}
		qslsReport = list.get(i);
		Date oldEst = (qslsReport.getOldest() == null || dateTimeCapture.before(qslsReport.getOldest()))
				? dateTimeCapture
				: qslsReport.getOldest();
		Date newEst = (qslsReport.getNewEst() == null || dateTimeCapture.after(qslsReport.getNewEst()))
				? dateTimeCapture
				: qslsReport.getNewEst();
		qslsReport.setCount(qslsReport.getCount() + 1);
		qslsReport.setOldest(oldEst);
		qslsReport.setNewEst(newEst);
		list.set(i, qslsReport);
	}
}