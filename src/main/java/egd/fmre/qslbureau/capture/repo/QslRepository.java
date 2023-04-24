package egd.fmre.qslbureau.capture.repo;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;

@Repository
public interface QslRepository extends JpaRepository<Qsl, Integer> {
    
    Set<Qsl> findBySlot(Slot slot);
    
    @Query(value = "SELECT Q From Query ")
    Set<Qsl> findByLocal(Slot slot);

}
