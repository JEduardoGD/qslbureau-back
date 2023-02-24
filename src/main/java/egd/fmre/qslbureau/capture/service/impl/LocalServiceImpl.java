package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.service.LocalService;

@Service
public class LocalServiceImpl implements LocalService {

    @Autowired
    LocalRepository localRepository;

    @Override
    public Local getById(int id) {
        return localRepository.findById(id);
    }
}