package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslsReport;
import egd.fmre.qslbureau.capture.dto.QslsReportKey;
import egd.fmre.qslbureau.capture.dto.SlotReport;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.ReportsService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportsServiceImpl implements ReportsService {

	@Autowired
	private SlotLogicService slotLogicService;
	@Autowired
	private QslCaptureService qslCaptureService;
	@Autowired
	private ZoneruleService zoneruleService;
	@Autowired
	private CapturerService capturerService;
	
	private Status statusQslVigente;
	
	@PostConstruct
	private void init() {
		statusQslVigente = qslCaptureService.getStatusQslVigente();
	}


	@Override
	public List<QslsReport> gettingCallsignsMap() {
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		List<QslsReport> list = new ArrayList<>();
		for (Slot slot : openedOrCreatedSlots) {
			try {
				List<QslDto> qsls = qslCaptureService.qslsBySlot(slot.getId()).stream()
						.filter(q -> q.getStatus() == statusQslVigente.getId().intValue())
						.collect(Collectors.toList());
				for (QslDto qsl : qsls) {
					String efectiveCallsign = qsl.getVia() != null ? qsl.getVia() : qsl.getTo();
					Zonerule zonerule = zoneruleService.findActiveByCallsign(efectiveCallsign);
					if (zonerule == null) {
						processCallsign(list, new QslsReportKey(qsl.getTo(), qsl.getVia()), qsl.getDateTimeCapture());
					}
				}
			} catch (QslcaptureException e) {
				log.error(e.getMessage());
			}
		}
		return list;
	}


	@Override
	public List<SlotReport> getQslsByCapturer(int idCapturer) {
		List<SlotReport> list = new ArrayList<>();
		Capturer capturer = capturerService.findById(idCapturer);
		if (capturer == null) {
			return null;
		}
		/*get all zonerules for capturer*/
		List<Zonerule> zoneRuleList = zoneruleService.findByCapturer(capturer);
		List<Slot> openedOrCreatedSlots = slotLogicService.getOpenedOrCreatedSlots();
		for (Zonerule z : zoneRuleList) {
			String zoneRuleCallsign = z.getCallsign();
			List<Slot> slots = openedOrCreatedSlots.stream()
					.filter(s -> s.getCallsignto() != null && s.getCallsignto().equalsIgnoreCase(zoneRuleCallsign))
					.collect(Collectors.toList());
			List<SlotReport> listSection = slots.stream().map(s -> {
				int c = 0;
				try {
					c = qslCaptureService.countQslsBySlot(s.getId());
				} catch (QslcaptureException e) {
					log.error(e.getMessage());
				}
				return new SlotReport(s, capturer, c);}).collect(Collectors.toList());
			list.addAll(listSection);
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