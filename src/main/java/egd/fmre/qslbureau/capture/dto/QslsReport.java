package egd.fmre.qslbureau.capture.dto;

import java.util.Date;

public class QslsReport {
	private QslsReportKey qslsReportKey;
	private int count;
	private Date oldest;
	private Date newEst;

	public QslsReport(QslsReportKey qslsReportKey) {
		super();
		this.qslsReportKey = qslsReportKey;
	}

	public QslsReportKey getQslsReportKey() {
		return qslsReportKey;
	}

	public void setQslsReportKey(QslsReportKey qslsReportKey) {
		this.qslsReportKey = qslsReportKey;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Date getOldest() {
		return oldest;
	}

	public void setOldest(Date oldest) {
		this.oldest = oldest;
	}

	public Date getNewEst() {
		return newEst;
	}

	public void setNewEst(Date newEst) {
		this.newEst = newEst;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qslsReportKey == null) ? 0 : qslsReportKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QslsReport other = (QslsReport) obj;
		if (qslsReportKey == null) {
			if (other.qslsReportKey != null)
				return false;
		} else if (!qslsReportKey.equals(other.qslsReportKey))
			return false;
		return true;
	}

}