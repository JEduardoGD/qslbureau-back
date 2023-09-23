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
    @Query("SELECT new egd.fmre.qslbureau.capture.dto.SlotCountqslDTO(s, count(*)) "
            + "FROM Slot s INNER JOIN Qsl qsl on qsl.slot = s " 
            + "where s.id in :slotsIds AND qsl.status.id = 1001 "
            + "GROUP BY s")
    List<SlotCountqslDTO> getQslsBySlot(@Param("slotsIds") List<Integer> slotsIds);
    
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
}
