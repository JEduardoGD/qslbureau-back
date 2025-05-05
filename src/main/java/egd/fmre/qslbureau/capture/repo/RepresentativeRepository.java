package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Zone;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Integer> {

	@Query("SELECT r FROM Representative r "
			+ "inner join RepresentativeZone rz on rz.representative.id = r.id "
			+ "inner join rz.zone z "
			+ "inner join Zonerule zr on zr.zone.id = z.id "
			+ "where zr.callsign = :callsign")
	List<Representative> getRepresentativesForCallsign(@Param("callsign") String callsign);
	
	@Query("SELECT r FROM Representative r "
			+ "inner join RepresentativeZone rz on rz.representative.id = r.id and (rz.start < CURRENT_TIMESTAMP and rz.end is null OR rz.start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < rz.end) "
	        + "inner join Zone z on z.id = rz.zone.id and (z.start < CURRENT_TIMESTAMP and z.end is null OR z.start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < z.end) "
	        + "where z = :zone and (r.start < CURRENT_TIMESTAMP and r.end is null OR r.start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < r.end)")
	List<Representative> getRepresentativesByZone(@Param("zone") Zone zone);
	
	@Query("SELECT r FROM Representative r "
	        + "where r.id = :id and (r.start < CURRENT_TIMESTAMP and r.end is null OR r.start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < r.end)")
	Representative getActiveRepresentativesById(@Param("id") int id);
} 