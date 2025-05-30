package egd.fmre.qslbureau.capture.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.dto.CapturedCallsign;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;

@Repository
public interface QslRepository extends JpaRepository<Qsl, Integer> {

    Set<Qsl> findBySlot(Slot slot);

    @Query(value = "SELECT Q From Qsl Q INNER JOIN Q.slot s INNER JOIN s.local l WHERE l = :local and s.status IN :slotStatuses")
    List<Qsl> findByPaggeable(@Param("local") Local slot, @Param("slotStatuses") List<Status> slotStatuses, Pageable pageable);

    @Query(value = "SELECT q From Qsl q INNER JOIN q.slot s "
            + "WHERE (q.to = :to or q.via = :via) and q.status = :qslStatus and s.status IN :slotStatuses")
    Set<Qsl> findQslsInSystem(
    		@Param("to") String to,
    		@Param("via") String via,
    		@Param("qslStatus") Status qslStatus,
            @Param("slotStatuses") List<Status> slotStatuses);

    @Query(value = "SELECT q From Qsl q WHERE q.slot = :slot and q.status IN :statuses")
    List<Qsl> findBySlotAndStatuses(@Param("slot") Slot slot, @Param("statuses") List<Status> statuses);

	@Query(value = "SELECT q From Qsl q INNER JOIN q.slot s "
			+ "WHERE ((q.to IN :callsignlist and q.via is null) or (q.to is not null and q.via IN :callsignlist)) "
			+ "and q.status = :qslStatus and s.status IN :slotStatuses and s.local = :local")
	List<Qsl> findQslsInSystem(@Param("callsignlist") List<String> callsignlist, @Param("qslStatus") Status qslStatus,
			@Param("slotStatuses") List<Status> slotStatuses, @Param("local") Local local);

	@Query(value = "SELECT C.CALLSIGN, C.OLDESTDATETIME FROM V_CAPTUREDCALLSIGNS C", nativeQuery = true)
	List<CapturedCallsign> getCapturedCallsigns();
}
