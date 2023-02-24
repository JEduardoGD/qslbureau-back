package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.CapturerLocal;
import egd.fmre.qslbureau.capture.repo.CapturerLocalRepository;
import egd.fmre.qslbureau.capture.service.CapturerLocalService;

@Service
public class CapturerLocalServiceImpl implements CapturerLocalService {
    @Autowired
    CapturerLocalRepository capturerLocalRepository;

    @Override
    public Set<CapturerLocal> findCapturerLocalActiveByCapturer(Capturer capturer) {
        Date nowDate = new Date();
        Set<CapturerLocal> capturerLocals = capturerLocalRepository.findByCapturer(capturer);
        Set<CapturerLocal> newSetcapturerLocals = capturerLocals.stream().filter(p -> p.getStart() != null)
                .filter(p -> {
                    if (p.getEnd() == null) {
                        return p.getStart().before(nowDate);
                    } else {
                        return p.getStart().before(nowDate) && nowDate.before(p.getEnd());
                    }
                }).collect(Collectors.toSet());
        return newSetcapturerLocals;
    }
}
