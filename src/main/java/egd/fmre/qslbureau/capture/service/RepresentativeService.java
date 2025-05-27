package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Zone;

public interface RepresentativeService {
	Representative findById(Integer id);

	List<Representative> getRepresentativesForCallsign(String callsign);

	List<Representative> getRepresentativesByZone(Zone zone);

	Representative getActiveRepresentativesById(int id);

	List<Representative> findAllActive();
}
