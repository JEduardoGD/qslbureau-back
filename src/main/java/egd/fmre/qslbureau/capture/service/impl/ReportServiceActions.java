package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;
import java.util.List;

import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;

public abstract class ReportServiceActions {

	protected void processCallsign(List<QslsReport> list, QslsReportKey key, Date dateTimeCapture) {
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
