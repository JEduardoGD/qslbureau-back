package egd.fmre.qslbureau.capture.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.QslCaptureService;

@RestController
@RequestMapping("qslcard")
public class QslcapturaController {

    @Autowired
    private QslCaptureService qslCaptureService;

    @PutMapping
    public ResponseEntity<StandardResponse> captureQsl(@RequestBody QslDto qsl, @RequestHeader Map<String, String> headers) {
        return new ResponseEntity<StandardResponse>(qslCaptureService.captureQsl(qsl),new HttpHeaders(),HttpStatus.CREATED);
    }

    @GetMapping("/countbyslot/{slotId}")
    public int countBySlot(@PathVariable int slotId) throws QslcaptureException {
        return qslCaptureService.countQslsBySlot(slotId);
    }

    @GetMapping("/byslot/{slotId}")
    public Set<QslDto> bySlot(@PathVariable int slotId) throws QslcaptureException {
        return qslCaptureService.qslsBySlot(slotId);
    }

    @GetMapping("/bylocalid/{localid}")
    public List<QslDto> byLocalId(@PathVariable int localid) throws QslcaptureException {
        return qslCaptureService.qslsByLocal(localid);
    }
}
