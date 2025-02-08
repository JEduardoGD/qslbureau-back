package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.repo.CapturerRepository;
import egd.fmre.qslbureau.capture.service.CapturerService;

@Service
public class CapturerServiceImpl implements CapturerService {

    @Autowired CapturerRepository capturerRepository;

    @Override
    public Capturer findById(int idCapturer) {
        return capturerRepository.findById(idCapturer).orElse(null);
    }

    @Override
    public Capturer getActiveCapturerById(int idCapturer) {
        Capturer capturer = capturerRepository.findById(idCapturer).orElse(null);
        if (capturer == null) {
            return null;
        }
        Date nowDate = new Date();
        if (capturer.getStart() == null) {
            return null;
        }
        if (capturer.getStart().before(nowDate) && capturer.getEnd() == null) {
            return capturer;
        }
        if (capturer.getEnd() != null && capturer.getStart().before(nowDate) && nowDate.before(capturer.getEnd())) {
            return capturer;
        }
        return null;
    }

    @Override
    public Capturer findByUsername(String username) {
        return capturerRepository.findByUsername(username);
    }
}
