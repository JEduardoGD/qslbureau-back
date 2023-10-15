package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
