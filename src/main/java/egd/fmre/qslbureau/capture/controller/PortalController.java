package egd.fmre.qslbureau.capture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.service.PortalService;
import egd.fmre.qslbureau.capture.service.QuerylogService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("portal")
public class PortalController {
    
    @Autowired PortalService   portalService;
    @Autowired QuerylogService querylogService;
    
    private int maxCallsignLeght = StaticValuesHelper.TWELVE;

    @GetMapping("/qslsfor/{callsign}")
    public ResponseEntity<StandardResponse> captureQsl(@PathVariable String callsign, HttpServletRequest request) {
        StandardResponse standardResponse;
        try {
            callsign = callsign.trim();
            if (callsign.length() > maxCallsignLeght) {
                callsign = callsign.substring(StaticValuesHelper.ZERO, maxCallsignLeght);
            }
            querylogService.newRegister(callsign, request.getRemoteAddr());
            callsign = callsign.toUpperCase();
            standardResponse = new StandardResponse(JsonParserUtil.parse(portalService.getQslInfoForCallsign(callsign)));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);

    }
    
}
