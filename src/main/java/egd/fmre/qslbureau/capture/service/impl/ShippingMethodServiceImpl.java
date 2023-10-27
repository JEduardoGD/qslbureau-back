package egd.fmre.qslbureau.capture.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.ShippingMethod;
import egd.fmre.qslbureau.capture.repo.ShippingMethodRepository;
import egd.fmre.qslbureau.capture.service.ShippingMethodService;

@Service
public class ShippingMethodServiceImpl implements ShippingMethodService {
    @Autowired
    ShippingMethodRepository shippingMethodRepository;

    @Override
    public List<ShippingMethod> getAll() {
        return shippingMethodRepository.findAll();
    }

    @Override
    public ShippingMethod findById(Integer shippingMethodId) {
        return shippingMethodRepository.findById(shippingMethodId).get();
    }

    @Override
    public ShippingMethod findByKey(String key) {
        return shippingMethodRepository.findByKey(key);
    }
}
