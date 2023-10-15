package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Capturer;

public interface CapturerService {

    Capturer findById(int idCapturer);

    Capturer findByUsername(String username);

    Capturer getActiveCapturerById(int idCapturer);
    
    List<Capturer> getCapturersforCallsign(String callsign);
}
