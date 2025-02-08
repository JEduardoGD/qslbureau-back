package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Representative;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Integer> {

	@Query("SELECT r FROM Representative r "
			+ "inner join RepresentativeZone rz on rz.representative.id = r.id "
			+ "inner join rz.zone z "
			+ "inner join Zonerule zr on zr.zone.id = z.id "
			+ "where zr.callsign = :callsign")
	List<Representative> getRepresentativesForCallsign(@Param("callsign") String callsign);
} 