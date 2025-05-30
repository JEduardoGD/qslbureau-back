package egd.fmre.qslbureau.capture.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Capturer;

@Repository
public interface CapturerRepository extends JpaRepository<Capturer, Integer> {
    public Capturer findByUsername(String username);
    
    /*
    @Query("select c from Capturer c "
            + "inner join Zonerule zr on zr.capturer = c "
            + "where zr.callsign = :callsign")
    public List<Capturer>getCapturersForCallsign(@Param("callsign") String callsign);
    */
}
