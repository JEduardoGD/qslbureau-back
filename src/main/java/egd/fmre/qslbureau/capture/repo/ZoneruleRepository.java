package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Zonerule;

@Repository
public interface ZoneruleRepository extends JpaRepository<Zonerule, Integer> {
	public List<Zonerule> findByCallsign(String callsign);
	public List<Zonerule> findByCapturer(Capturer capturer);
}
