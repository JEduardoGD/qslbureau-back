package egd.fmre.qslbureau.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.PurgueEmptySlotsService;

@Service
public class PurgueEmptySlotsServiceImpl implements PurgueEmptySlotsService {
    
    @Autowired private SlotRepository slotRepository;
    @Autowired private QslRepository  qslRepository;
    
    @Override
    public void purgue() {
    }
}
