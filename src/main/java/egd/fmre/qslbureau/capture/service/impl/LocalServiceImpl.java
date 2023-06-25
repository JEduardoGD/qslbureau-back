package egd.fmre.qslbureau.capture.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.CapturerLocal;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.service.CapturerLocalService;
import egd.fmre.qslbureau.capture.service.LocalService;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    LocalRepository localRepository;

    @Autowired
    CapturerLocalService capturerLocalService;

    @Override
    public Local getById(int id) {
        return localRepository.findById(id);
    }

    @Override
    public Set<Local> findByCapturer(Capturer capturer) {
        // 1 find CapturerLocal objects
        Set<CapturerLocal> capturerLocals = capturerLocalService.findCapturerLocalActiveByCapturer(capturer);

        return localRepository.findByCapturerLocalsIn(capturerLocals);
    }
}