package egd.fmre.qslbureau.capture.service.impl;

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
}
