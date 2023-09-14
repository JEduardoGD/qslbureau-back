package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.SummaryQslDto;

public interface PortalService {

    SummaryQslDto getQslInfoForCallsign(String callsign);

}
