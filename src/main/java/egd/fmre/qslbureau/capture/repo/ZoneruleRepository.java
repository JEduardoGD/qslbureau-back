package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Zone;
import egd.fmre.qslbureau.capture.entity.Zonerule;

@Repository
public interface ZoneruleRepository extends JpaRepository<Zonerule, Integer> {
	public List<Zonerule> findByCallsign(String callsign);
	
	@Query(""
			+ "SELECT zr FROM Zonerule zr WHERE zr.zone = :zone "
			+ "AND ((zr.start < NOW() AND zr.end IS NULL) OR (zr.start < NOW() AND NOW() < zr.end))")
	public List<Zonerule> findAllActivesByZone(@Param("zone") Zone zone);
}
