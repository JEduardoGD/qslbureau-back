package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import egd.fmre.qslbureau.capture.entity.ShippingMethod;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Integer> {
    
    ShippingMethod findByKey(String key);
}
