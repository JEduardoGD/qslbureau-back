package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Representative;

public interface RepresentativeService {
    Representative findById(Integer id);

	List<Representative> getRepresentativesForCallsign(String callsign);
}
