package egd.fmre.qslbureau.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.repo.ZoneRepository;
import egd.fmre.qslbureau.capture.service.ZoneService;

@Service
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    ZoneRepository zoneRepository;

    @Override
    public Zone findById(Integer id) {
        return zoneRepository.findById(id).get();
    }
    
    @Override
    public List<Zone> getActiveZonesByRepresentative(Representative representative) {
    	return zoneRepository.getActiveZonesByRepresentative(representative);
    }
}
