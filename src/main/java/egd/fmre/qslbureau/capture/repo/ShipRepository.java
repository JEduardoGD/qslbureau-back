package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Ship;
import egd.fmre.qslbureau.capture.entity.Slot;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Integer> {
    Ship findBySlot(Slot slot);
}
