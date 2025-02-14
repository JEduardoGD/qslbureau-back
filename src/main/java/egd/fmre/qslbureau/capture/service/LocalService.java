package egd.fmre.qslbureau.capture.service;

import java.util.List;
import java.util.Set;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;

public interface LocalService {

    Local getById(int id);

    Set<Local> findByCapturer(Capturer capturer);

    List<Local> getActiveLocals();

	Set<Local> getLocalsForCapturer(Capturer capturer);
}
