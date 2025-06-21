package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	@Query("SELECT c FROM Contact c WHERE c.callsign = :callsign and ((c.start < now() and c.end is null) or (c.start < now() and now() < c.end))")
	public List<Contact> findActiveForCallsign(@Param("callsign") String callsign);
	
	@Query("SELECT c FROM Contact c WHERE c.id = :id and ((c.start < now() and c.end is null) or (c.start < now() and now() < c.end))")
	public Contact findActiveById(@Param("id") Integer id);
}
