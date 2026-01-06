package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;
import egd.fmre.qslbureau.capture.service.impl.WorldBuroesServiceImplException;

public interface WorldBuroesService {

    List<BuroDto> findByCallsign(String callsing, int localId) throws WorldBuroesServiceImplException;

}
