package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dao.Qsl;
import egd.fmre.qslbureau.capture.service.QslCaptureService;

@Service
public class QslCaptureServiceImpl implements QslCaptureService {
	@Override
	public Qsl captureQsl(Qsl qsl) {
		return qsl;
	}
}
