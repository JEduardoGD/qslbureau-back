package egd.fmre.qslbureau.capture.repo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.dto.QslRuleDto;
import egd.fmre.qslbureau.capture.entity.CallsignRule;

@Repository
public interface CallsignruleRepository extends JpaRepository<CallsignRule, Integer> {
	
	
	final static String Q = ""+
			"SELECT qsl.IDQSL as Idqsl, csr.IDCALLSIGNRULE as Idcallsignrule  " +
			"FROM T_QSL qsl " +
			"INNER JOIN T_SLOT slot ON slot.IDSLOT = qsl.IDSLOT " +
			"INNER JOIN C_LOCAL loccal on loccal.IDLOCAL = slot.IDLOCAL " +
			"INNER JOIN C_CALLSIGNRULE csr on ( " +
			"    (csr.CALLSIGNTO = qsl.TOCALLSIGN AND qsl.VIA IS NULL) OR (csr.CALLSIGNTO = qsl.VIA AND qsl.TOCALLSIGN IS NOT NULL) AND " +
			"    ((csr.START < CURDATE() AND csr.END IS NULL) OR (csr.START < CURDATE() AND CURDATE() < csr.END))  " +
			") " +
			"WHERE loccal.IDLOCAL = :idlocal " +
			"AND slot.IDSTATUS IN (2001, 2002) " +
			"AND qsl.IDSTATUS = 1001 " +
			"AND slot.CALLSIGNTO != csr.CALLSIGNREDIRECT ";
	
	public List<CallsignRule> findByCallsignTo(String callsignTo);

	@Query("SELECT c FROM CallsignRule c "
			+ "WHERE (c.start < :nowdate and :nowdate < c.end) or (c.start < :nowdate and c.end is null) "
			+ "and (c.callsignTo = :callsignTo)")
	public List<CallsignRule> getActiveRulesForCallsign(@Param("nowdate") Date nowdate,
			@Param("callsignTo") String callsignTo);

	@Query("SELECT c FROM CallsignRule c "
			+ "WHERE (c.start < :nowdate and :nowdate < c.end) or (c.start < :nowdate and c.end is null)")
	public List<CallsignRule> getActiveRules(@Param("nowdate") Date nowdate);

	@Query(value = Q, nativeQuery = true)
	public Collection<QslRuleDto> getQslsRules(@Param("idlocal") int idlocal);
}
