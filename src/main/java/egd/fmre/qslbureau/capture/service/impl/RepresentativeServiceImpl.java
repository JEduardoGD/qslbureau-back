package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.repo.RepresentativeRepository;
import egd.fmre.qslbureau.capture.service.RepresentativeService;

@Service
public class RepresentativeServiceImpl implements RepresentativeService {
    @Autowired
    private RepresentativeRepository representativeRepository;

    @Override
    public Representative findById(Integer id) {
        return representativeRepository.findById(id).orElse(null);
    }
}
