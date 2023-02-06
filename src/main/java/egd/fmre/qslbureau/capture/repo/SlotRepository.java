package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Integer> {
    
    @Query("SELECT s FROM Slot s WHERE s.callsignto = :callsignto and s.open IS NOT NULL and s.closed IS NULL AND s.local = :local")
    Slot getOpenedSlotForCallsign(@Param("callsignto") String callsign, @Param("local") Local local);
    
    @Query("SELECT s FROM Slot s WHERE s.open IS NOT NULL and s.closed IS NULL AND s.local = :local")
    List<Slot> getOpenedSlotsInLocal(@Param("local") Local local);
}
