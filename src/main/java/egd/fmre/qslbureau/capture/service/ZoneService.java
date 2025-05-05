package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Zone;

public interface ZoneService {

    Zone findById(Integer id);

	List<Zone> getActiveZonesByRepresentative(Representative representative);

}
