package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {
	@Query(""
			+ "SELECT z "
			+ "FROM Zone z "
			+ "INNER JOIN RepresentativeZone rz ON rz.zone = z AND ((rz.start < NOW() AND rz.end IS NULL) OR (rz.start < NOW() AND NOW() < rz.end)) "
			+ "INNER JOIN Representative r ON rz.representative = r "
			+ "WHERE r = :representative "
			+ "AND ((r.start < NOW() AND r.end IS NULL) OR (r.start < NOW() AND NOW() < r.end)) "
			+ "AND ((z.start < NOW() AND z.end IS NULL) OR (z.start < NOW() AND NOW() < z.end)) ")
	public List<Zone> getActiveZonesByRepresentative(@Param("representative") Representative representative);
}
