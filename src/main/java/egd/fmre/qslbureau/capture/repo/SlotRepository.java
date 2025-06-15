package egd.fmre.qslbureau.capture.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Integer> {
    static final String QUERY_CLOSEABLE_SLOTS = "" +
            "SELECT TBL.IDSLOT FROM (" +
            "    SELECT SLOT.IDSLOT, SLOT.SLOTNUMBER, COUNT(*) AS 'C' FROM T_SLOT SLOT" +
            "    INNER JOIN T_QSL QSL ON QSL.IDSLOT = SLOT.IDSLOT" +
            "    INNER JOIN C_STATUS SLOT_STATUS ON SLOT_STATUS.IDSTATUS = SLOT.IDSTATUS" +
            "    INNER JOIN C_STATUS QSL_STATUS  ON  QSL_STATUS.IDSTATUS = QSL.IDSTATUS" +
            "    WHERE SLOT.IDLOCAL = :IDLOCAL" +
            "    AND SLOT_STATUS.STATUS IN ('OPEN', 'CREATED') AND QSL_STATUS.STATUS IN ('VIGENTE')" +
            "    GROUP BY SLOT.IDSLOT" +
            ") TBL WHERE TBL.C <= 0" ;
    
    static final String QUERY_OPENABLE_SLOTS = "" +
            "SELECT TBL.IDSLOT, TBL.C FROM (" +
            "    SELECT SLOT.IDSLOT, SLOT.SLOTNUMBER, COUNT(*) AS 'C' FROM T_SLOT SLOT" +
            "    INNER JOIN T_QSL QSL ON QSL.IDSLOT = SLOT.IDSLOT" +
            "    INNER JOIN C_STATUS SLOT_STATUS ON SLOT_STATUS.IDSTATUS = SLOT.IDSTATUS" +
            "    INNER JOIN C_STATUS QSL_STATUS  ON  QSL_STATUS.IDSTATUS = QSL.IDSTATUS" +
            "    WHERE SLOT.IDLOCAL = :IDLOCAL" +
            "    AND SLOT_STATUS.STATUS IN ('CREATED') AND QSL_STATUS.STATUS IN ('VIGENTE')" +
            "    GROUP BY SLOT.IDSLOT" +
            ") TBL WHERE TBL.C > 0" ;
    
    @Query("SELECT new egd.fmre.qslbureau.capture.dto.SlotCountqslDTO(s, count(*)) "
            + "FROM Slot s INNER JOIN Qsl qsl on qsl.slot = s " 
            + "where s.id in :slotsIds AND qsl.status.id = 1001 "
            + "GROUP BY s")
    List<SlotCountqslDTO> getQslsBySlotIdList(@Param("slotsIds") List<Integer> slotsIds);
    
    @Query("SELECT s FROM Slot s WHERE s.status IN :statuses AND s.local = :local")
	List<Slot> findByStatusesForLocal(@Param("statuses") List<Status> statuses, @Param("local") Local local);
    
	@Query("SELECT s FROM Slot s WHERE s.country = :country AND s.status IN :statuses AND s.local = :local")
	List<Slot> findByLocalAndCountryAndStatuses(@Param("country") String country,
			@Param("statuses") List<Status> statuses, @Param("local") Local local);
	
	@Query("SELECT max(s.slotNumber) FROM Slot s WHERE s.status IN :statuses AND s.local = :local")
	Integer getLastSlotNumberByLocal(@Param("statuses") List<Status> statuses, @Param("local") Local local);
	
	@Query("SELECT s FROM Slot s WHERE s.status IN :slotStatuses")
	List<Slot> findByStatusesForLocal(@Param("slotStatuses") List<Status> slotStatuses);
  
	List<Slot> findByLocal(Local local);
	
	@Query("SELECT s FROM Slot s WHERE s.status IN :slotStatuses and s.callsignto is null and s.country is null")
	List<Slot> getNullSlots(@Param("slotStatuses") List<Status> slotStatuses);
	
	@Query(value = QUERY_CLOSEABLE_SLOTS, nativeQuery = true)
    List<Integer> gettingCloseableSlotIds(@Param("IDLOCAL") int idlocal);
    
    @Query(value = QUERY_OPENABLE_SLOTS, nativeQuery = true)
    List<Integer> gettingOpenableSlotIds(@Param("IDLOCAL") int idlocal);
    
    @Query("SELECT s FROM Slot s WHERE s.callsignto = :callsignto AND s.status IN (:statuses)")
    List<Slot> getSlotByCallsignAndStatuses(@Param("callsignto") String callsignto, @Param("statuses") List<Status> statuses);
}
