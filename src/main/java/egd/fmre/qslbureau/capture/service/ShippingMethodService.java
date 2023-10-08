package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.ShippingMethod;

public interface ShippingMethodService {

    List<ShippingMethod> getAll();
    
    ShippingMethod findById(Integer shippingMethodId);
    
    ShippingMethod findByKey(String key);

}
