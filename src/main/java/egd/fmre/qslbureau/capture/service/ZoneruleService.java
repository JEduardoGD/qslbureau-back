package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Zonerule;

public interface ZoneruleService {

	Zonerule findActiveByCallsign(String callsign);

}
