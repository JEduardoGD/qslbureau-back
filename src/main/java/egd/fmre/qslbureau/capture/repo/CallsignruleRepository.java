package egd.fmre.qslbureau.capture.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.entity.CallsignRule;

@Repository
public interface CallsignruleRepository extends JpaRepository<CallsignRule, Integer> {
    public List<CallsignRule> findByCallsignTo(String callsignTo);
    
    @Query("SELECT c FROM CallsignRule c "
            + "WHERE (c.start < :nowdate and :nowdate < c.end) or (c.start < :nowdate and c.end is null) "
            + "and (c.callsignTo = :callsignTo)")
    public List<CallsignRule> getActiveRulesForCallsign(@Param("nowdate") Date nowdate,@Param("callsignTo") String callsignTo);
    
    @Query("SELECT c FROM CallsignRule c "
            + "WHERE (c.start < :nowdate and :nowdate < c.end) or (c.start < :nowdate and c.end is null)")
    public List<CallsignRule> getActiveRules(@Param("nowdate") Date nowdate);
}
