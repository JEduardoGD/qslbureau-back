package egd.fmre.qslbureau.capture.service;

import java.util.Set;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;

public interface LocalService {

    Local getById(int id);

    Set<Local> findByCapturer(Capturer capturer);

}
