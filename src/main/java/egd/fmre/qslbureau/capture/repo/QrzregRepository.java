package egd.fmre.qslbureau.capture.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import egd.fmre.qslbureau.capture.entity.Qrzreg;

public interface QrzregRepository extends JpaRepository<Qrzreg, Integer> {

    @Query("SELECT r FROM Qrzreg r WHERE r.callsign = :callsign and :date <= r.updatedAt")
    List<Qrzreg> findByCallsignInPeriod(@Param("callsign") String callsign, @Param("date") Date date);
}
