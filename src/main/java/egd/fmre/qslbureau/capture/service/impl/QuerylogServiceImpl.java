package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Querylog;
import egd.fmre.qslbureau.capture.repo.QuerylogRepository;
import egd.fmre.qslbureau.capture.service.QuerylogService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@Service
public class QuerylogServiceImpl implements QuerylogService{
    
    @Autowired QuerylogRepository  querylogRepository;
    
    @Override
    public void newRegister(String callsign, String ip) {
        Querylog querylog  = new Querylog();
        querylog.setDatetime(DateTimeUtil.getDateTime());
        querylog.setIp(ip);
        querylog.setQuery(callsign);
        querylogRepository.save(querylog);
    }
}
