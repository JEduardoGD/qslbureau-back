package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Capturer;

public interface CapturerService {

    Capturer findById(int idCapturer);

    Capturer findByUsername(String username);

    Capturer getActiveCapturerById(int idCapturer);
}
