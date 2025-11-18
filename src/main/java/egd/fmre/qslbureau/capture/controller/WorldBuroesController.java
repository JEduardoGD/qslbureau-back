package egd.fmre.qslbureau.capture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.service.WorldBuroesService;
import egd.fmre.qslbureau.capture.service.impl.WorldBuroesServiceImplException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("buroes")
@Slf4j
public class WorldBuroesController {

    @Autowired
    WorldBuroesService worldBuroesService;

    @GetMapping("/findByCallsing/{localsign}")
    public ResponseEntity<StandardResponse> getApplicableRules(@PathVariable String localsign) {
    List<BuroDto> buroes;
    try {
        buroes = worldBuroesService.findByCallsign(localsign);
    } catch (WorldBuroesServiceImplException e) {
        return new ResponseEntity<StandardResponse>(new StandardResponse(true, e.getMessage()), new HttpHeaders(),
            HttpStatus.OK);
    }
    StandardResponse standardResponse = new StandardResponse(buroes);
    return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.OK);
    }
}
