package egd.fmre.qslbureau.capture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.service.QslCaptureService;

@RestController
@RequestMapping("qslcard")
public class QslcapturaController {

    @Autowired
    private QslCaptureService qslCaptureService;

    @PutMapping
    public QslDto captureQsl(@RequestBody QslDto qsl) {
        return qslCaptureService.captureQsl(qsl);
    }
}
