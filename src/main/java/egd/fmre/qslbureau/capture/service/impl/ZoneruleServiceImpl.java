package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.entity.Zonerule;
import egd.fmre.qslbureau.capture.repo.ZoneruleRepository;
import egd.fmre.qslbureau.capture.service.ZoneruleService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@Service
public class ZoneruleServiceImpl implements ZoneruleService {
	@Autowired
	private ZoneruleRepository zoneruleRepository;

	@Override
	public Zonerule findActiveByCallsign(String callsign) {
		List<Zonerule> zonerules = zoneruleRepository.findByCallsign(callsign);
		if(zonerules == null || zonerules.isEmpty()) {
			return null;
		}
		Date now = DateTimeUtil.getDateTime();
		return zonerules.stream().filter(zr -> (zr.getStart().before(now) && zr.getEnd() == null)
				|| (zr.getEnd() != null && zr.getStart().before(now) && now.before(zr.getEnd())))
		.findFirst().orElse(null);
	}

    @Override
    public Zonerule findById(Integer id) {
        return zoneruleRepository.findById(id).get();
    }

	@Override
	public List<Zonerule> getAllActives() {
		List<Zonerule> zonerules = zoneruleRepository.findAll();
		Date now = DateTimeUtil.getDateTime();
		return zonerules.stream()
				.filter(zr -> (zr.getStart().before(now) && zr.getEnd() == null)
						|| (zr.getEnd() != null && zr.getStart().before(now) && now.before(zr.getEnd())))
				.collect(Collectors.toList());
	}

	@Override
	public List<Zonerule> getAllActivesByZone(Zone zone) {
		return zoneruleRepository.findAllActivesByZone(zone);
	}
}
