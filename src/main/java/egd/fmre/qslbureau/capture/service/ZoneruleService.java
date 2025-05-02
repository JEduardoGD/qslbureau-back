package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Zonerule;

public interface ZoneruleService {

	Zonerule findActiveByCallsign(String callsign);

    Zonerule findById(Integer id);

	List<Zonerule> getAllActives();
}
