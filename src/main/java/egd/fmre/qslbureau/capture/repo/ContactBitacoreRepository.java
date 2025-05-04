package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.dto.CallsignDatecontactDto;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;

@Repository
public interface ContactBitacoreRepository extends JpaRepository<ContactBitacore, Integer> {
	
	@Query("SELECT new egd.fmre.qslbureau.capture.dto.CallsignDatecontactDto(c.callsign, cb.slot.id, max(cb.datetime)) "
			+ "FROM ContactBitacore cb "
			+ "INNER JOIN cb.contact c "
			+ "WHERE c.callsign IN (:callsigns) "
			+ "GROUP BY c.callsign, cb.slot.id")
	public List<CallsignDatecontactDto> getContactOnCallsignList(@Param("callsigns") List<String> callsigns);

}
