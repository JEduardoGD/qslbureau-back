package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.service.PortalService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;

@Service
public class PortalServiceImpl implements PortalService {
    
    @Autowired QslCaptureService qslCaptureService;
    
    @Override
    public SummaryQslDto getQslInfoForCallsign(String callsign) {
        return qslCaptureService.getActiveQslsForCallsign(callsign);
    }
}
