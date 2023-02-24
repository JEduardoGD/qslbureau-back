package egd.fmre.qslbureau.capture.service;

import java.util.Set;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.CapturerLocal;

public interface CapturerLocalService {

    Set<CapturerLocal> findCapturerLocalActiveByCapturer(Capturer capturer);

}
